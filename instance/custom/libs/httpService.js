function isString(o) {
    return typeof o === 'string' || o instanceof String;
}


httpService = class HttpService {
    constructor(handler, editor) {
        this.editor = editor;
        this.handler = handler;
        this.cookie = 0;
        this.callbacks = {};
        this.cmds = {};
        this.fileChangeVersion = String(new Date().getTime());

        const host = hteditor_config.host || window.location.hostname;
        const port = hteditor_config.port || window.location.port;
        const url = this.url = window.location.protocol + '//' + host + ':' + port+'/htwebPre';

        setTimeout(() => {
            this.handler({ type: 'connected', message: url });
        }, 1);

        if (hteditor_config.checkForFileChanges && hteditor_config.checkForFileChangesInterval) {
            this.fileChangedEvent();
        }
    }

    request(cmd, data, callback) {

        const cookie = ++this.cookie;
        this.callbacks[cookie] = callback;
        this.cmds[cookie] = cmd;

        var sid = this.editor.sid;
        this[cmd](cookie, data, sid ? sid : null, callback);
        let message = cmd;
        if (data) {
            if (isString(data)) {
                message = cmd + ': ' + data;
            }
            else if (data.path) {
                message = cmd + ': ' + data.path;
            }
        }
        this.handler({ type: 'request', message, cmd, data });
    }

    fileChangedEvent() {
        var self = this,
            rUrl = this.url + '/fileVersion';
        rUrl += '/' + self.fileChangeVersion;
        // rUrl += '?sid=' + this.editor.sid;

        self.send('GET', rUrl, null, function (r) {
            self.fileChangeVersion = String(new Date().getTime());
            var response = r.target.response;
            if (response) {
                response = JSON.parse(response);
                var paths = Object.keys(response);
                paths.forEach(function (path) {
                    self.handler({ type: 'fileChanged', path: path });
                });
            }
        });

        setTimeout(function() {
            self.fileChangedEvent();
        }, hteditor_config.checkForFileChangesInterval);
    }

    export(cookie, dir, sid) {
        var self = this,
            xhr = new ht.Request(),
            opt = {},
            formData = new FormData(),
            rUrl = this.url + '/export',
            params = '';
        // rUrl += '?sid=' + sid;

        formData.append('dir', dir);

        opt.url = encodeURI(rUrl);
        opt.method = 'POST';
        opt.data = formData;

        xhr.setResponseType('blob');
        xhr.onload = function (e) {
            var res = e.target;
            if (res.status == 200 || res.status === 0) {
                var response = res.response;
                var disposition = res.getResponseHeader('Content-Disposition').split(';'),
                    fileName = disposition[1].split('=')[1];

                var bolb = new Blob([response], {type:'application/zip'});
                var link = document.createElement('a');
                link.href = window.URL.createObjectURL(bolb);
                link.download = decodeURI(fileName);
                link.click();
                self.handleRespone(cookie, true);
            }
        }
        xhr.send(opt);
    }
    mkdir(cookie, dir, sid) {
        var self = this,
            xhr = new ht.Request(),
            opt = {},
            // formData = new FormData(),
            rUrl = this.url + '/mkdir';
        // rUrl += '?sid=' + sid;

        // formData.append('path', dir);
        var data2 = {"path":dir};

        opt.url = rUrl;
        // opt.url = encodeURI(rUrl);
        opt.method = 'POST';
        opt.data = JSON.stringify(data2);

        xhr.onload = function (e) {
            var res = e.target;
            if (res.status == 200 || res.status === 0) {
                var response = res.response || res.responseText;
                self.handleRespone(cookie, response === 'true');
                self.handler({ type: 'fileChanged', path: dir });
            }
        };
        xhr.send(opt);
    }
    explore(cookie, dir, sid) {
        var self = this,
            xhr = new ht.Request(),
            opt = {},
            rUrl = this.url + '/explore';
        rUrl += dir;
        // rUrl += '?sid=' + sid;

        opt.url = encodeURI(rUrl);

        xhr.onload = function (e) {
            var res = e.target;
            if (res.status == 200 || res.status === 0) {
                var response = res.response || res.responseText;
                self.handleRespone(cookie, JSON.parse(response));
            }
        }
        xhr.send(opt);
    }
    upload(cookie, data, sid) {
        var self = this,
            formData = new FormData(),
            rUrl = this.url + '/upload',
            filePath = data.path;
        // rUrl += '?sid=' + sid;

        formData.append('path', data.path);
        formData.append('content', data.content);

        self.send('POST', rUrl, formData, function (e) {
            var res = e.target;
            if (res.status == 200 || res.status === 0) {
                var response = res.response;
                self.handleRespone(cookie, true);
                if (response) {
                    var data = JSON.parse(response);
                    self.handler({ type: 'confirm', path: data.tempName, datas: data.paths});
                } else {
                    self.handler({ type: 'fileChanged', path: filePath });
                }
            }
        });
    }
    rename(cookie, data, sid) {
        var self = this,
            formData = new FormData(),
            newPath = data.new,
            oldPath = data.old,
            rUrl = this.url + '/rename';
        // rUrl += '?sid=' + sid;

        // formData.append('newPath', newPath);
        // formData.append('oldPath', oldPath);
        var data2 = {
            "newPath":newPath,
            "oldPath":oldPath
        };

        self.send('POST', rUrl, JSON.stringify(data2), function (e) {
            var res = e.target;
            if (res.status == 200 || res.status === 0) {
                var response = res.response || res.responseText;
                self.handleRespone(cookie, response === 'true');
                if (response === 'true') {
                    self.handler({ type: 'fileChanged', path: newPath });
                    self.handler({ type: 'fileChanged', path: oldPath });
                }
            }
        });
    }
    remove(cookie, data, sid) {
        var self = this,
            formData = new FormData(),
            rUrl = this.url + '/remove';
        // rUrl += '?sid=' + sid;

        // formData.append('path', data);
        var data2 = {
            'path':data
        };

        self.send('POST', rUrl, JSON.stringify(data2), function (e) {
            var res = e.target;
            if (res.status == 200 || res.status === 0) {
                var response = res.response || res.responseText;
                self.handleRespone(cookie, response === 'true');
                if (response === 'true') self.handler({ type: 'fileChanged', path: data });
            }
        });
    }
    locate(cookie, data, sid) {
        var self = this,
            rUrl = this.url + '/locate';
        rUrl += '?dir=' + data;
        // rUrl += '&sid=' + sid;

        self.send('GET', rUrl, null, function (e) {
            var res = e.target;
            if (res.status == 200 || res.status == 0) {
                self.handleRespone(cookie, true);
            }
        });
    }
    open(cookie, data, sid) {
        var self = this,
            rUrl = this.url + '/open';
        rUrl += '?url=' + data;
        // rUrl += '&sid=' + sid;

        self.send('GET', rUrl, null, function (e) {
            var res = e.target;
            if (res.status == 200 || res.status == 0) {
                self.handleRespone(cookie, true);
            }
        });
    }
    source(cookie, data, sid) {
        var self = this,
            formData = new FormData(),
            rUrl = this.url + '/source';
        // rUrl += '?sid=' + sid;

        // formData.append('url', data.url);
        // formData.append('encoding', data.encoding);
        // formData.append('prefix', data.prefix);

        var data2 = {
            'url':data.url,
            'encoding':data.encoding,
            'prefix':data.prefix
        };

        self.send('POST', rUrl, JSON.stringify(data2), function (e) {
            var res = e.target;
            if (res.status == 200 || res.status === 0) {
                var response = res.response || res.responseText;
                self.handleRespone(cookie, response);
            }
        });

    }
    import(cookie, data, sid) {
        var self = this,
            formData = new FormData(),
            rUrl = this.url + '/import';
        // rUrl += '?sid=' + sid;

        formData.append('path', data.path);
        formData.append('move', data.move);

        self.send('POST', rUrl, formData, function (e) {
            var res = e.target;
            if (res.status == 200 || res.status === 0) {
                self.handleRespone(cookie, true);
            }
        });
    }

    send(method, url, data, cb) {

        var xhr = new ht.Request();
        var opt = {};

        opt.url = encodeURI(url);
        opt.method = method;
        opt.data = data;

        if (cb) xhr.onload = cb;
        xhr.send(opt);
    }

    handleRespone(cookie, data) {
        const callback = this.callbacks[cookie];
        const cmd = this.cmds[cookie];
        delete this.callbacks[cookie];
        delete this.cmds[cookie];
        if (callback) callback(data);
        this.handler({ type: 'response', message: cmd, cmd, data });
    }
}

