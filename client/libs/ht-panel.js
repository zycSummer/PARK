!function(h,d,B){"use strict";var U="ht",i=h[U],D=i.Default,$=D.isTouchable,_=i.Color,T="px",F="0",Y="innerHTML",v="className",L="position",K="absolute",t="width",j="height",S="left",G="top",s="right",m="bottom",y="max-width",e="max-height",M=null,W="none",H="",l=h.parseInt,C=h.setTimeout,O=D.getInternal(),o=D.animate,w=_.titleIconBackground,u=function(){return document},c=function(O){return u().createElement(O)},I=function(){return c("div")},g=function(){return c("canvas")},x=function(D,B,P){D.style.setProperty(B,P,M)},k=function(l,y){return l.style.getPropertyValue(y)},Q=function(r,V){r.appendChild(V)},R=function(u,W){u.removeChild(W)},q=O.addEventListener,N=(O.removeEventListener,function(W){var b=W.scrollWidth,c=W.scrollHeight;return W===u().body&&(b=Math.max(b,u().documentElement.scrollWidth),c=Math.max(c,u().documentElement.scrollHeight)),{width:b,height:c}}),J=function(C){var F=C.touches[0];return F?F:C.changedTouches[0]};O.addMethod(D,{panelExpandIcon:{width:16,height:16,comps:[{type:"triangle",rect:[4,4,10,8],background:w,rotation:3.14}]},panelCollapseIcon:{width:16,height:16,comps:[{type:"triangle",rect:[4,4,10,8],background:w}]},panelLockIcon:{width:100,height:100,comps:[{type:"roundRect",rect:[10,50,80,40],borderWidth:10,borderColor:w},{type:"shape",points:[37,45,37,20,37,13,43,13,63,13,69,13,70,19,70,44],segments:[1,2,3,2,3,2],borderWidth:10,borderColor:w}]},panelUnLockIcon:{width:100,height:100,comps:[{type:"roundRect",rect:[10,50,80,40],borderWidth:10,borderColor:w},{type:"shape",points:[37,45,37,20,37,13,43,13,63,13,69,13,70,19,70,26],segments:[1,2,3,2,3,2],borderWidth:10,borderColor:w}]},panelMinimizeIcon:{width:100,height:100,comps:[{type:"shape",points:[10,35,35,35,35,10],segments:[1,2,2],borderWidth:8,borderColor:w},{type:"shape",points:[90,35,65,35,65,10],segments:[1,2,2],borderWidth:8,borderColor:w},{type:"shape",points:[10,65,35,65,35,90],segments:[1,2,2],borderWidth:8,borderColor:w},{type:"shape",points:[65,90,65,65,90,65],segments:[1,2,2],borderWidth:8,borderColor:w}]},panelRestoreIcon:{width:300,height:300,comps:[{type:"rect",rect:[10,24,268,56],background:w},{type:"rect",rect:[10,118,268,56],background:w},{type:"rect",rect:[10,213,268,56],background:w}]},panelTitleLabelColor:D.labelSelectColor,panelTitleLabelFont:D.labelFont,panelContentLabelFont:D.labelFont,panelTitleBackground:_.titleBackground,panelSeparatorWidth:1,panelSeparatorColor:B},!0);var E=i.widget.Panel=function(k){var R=this,C=R._view=O.createView(null,R);R.$1i=0,R.$18i=new i.Notifier,R.$2i="leftTop",x(C,G,F),x(C,S,F),x(C,L,K),x(C,"overflow","hidden"),R._interactor=new P(R),R.setConfig(k),R.addEventListener(function(S){var B=R.getPanelConfig(S.id),X=B.content;("beginRestore"===S.kind||"betweenResize"===S.kind||"endToggle"===S.kind)&&(X&&X.invalidate&&X.invalidate(),B.items&&B.items.forEach(function(x){x&&x.content&&x.content.invalidate&&x.content.invalidate()}))})};D.def(E,d,{ms_v:1,_dragContainment:"parent",setConfig:function(o){function V(H){H.expanded=!H.expanded,P.togglePanel(H.id,!0,!0)}if(o){for(var P=this,A=P._view.parentNode;this._view.children.length;)this._view.removeChild(this._view.children[0]);P._config=o,P.$35i=[],o.expanded==M&&(o.expanded=o.expand!=M?o.expand:!0);var S=P._view,J=P.$24i(o,S,!0),C=J[2],X=o.width;P.$35i.push(J[1]),o.items&&o.items.forEach(function(l){l.expanded==M&&(l.expanded=l.expand!=M?l.expand:!0);var D=P.$24i(l,C.children[0]);P.$35i.push(D[1])});var Z=I();x(Z,t,10+T),x(Z,j,10+T),x(Z,L,K),x(Z,m,F),x(Z,s,F),Z[v]="resize-area",Q(S,Z),P.$10i(),o.flowLayout&&x(S,L,"relative"),x(S,"opacity",F),Q(u().body,S),X==M&&(X=S.offsetWidth),x(S,t,X+T),x(S,y,X+T);var B=o.content;B&&B.isSelfViewEvent&&(B.setX(0),B.setY(0),B.setWidth(X-2*(o.borderWidth||0)),B.setHeight(o.contentHeight)),o.items&&o.items.forEach(function(p){V(p)}),o.buttons&&o.buttons.indexOf("toggle")<0||V(o),o.minimized==M&&o.minimize!=M&&(o.minimized=o.minimize),o.minimized&&o.minimizable!==!1&&P.minimize(!0),R(u().body,S),x(S,"opacity",H),A&&Q(A,S),P.iv()}},getPanelConfig:function(Z){var v=this,Q=v._config,x=Q.items;if(Q.id===Z)return Q;if(x)for(var M=0;M<x.length;M++){var C=x[M];if(C.id===Z)return C}},getPanelView:function(H){for(var z=this,I=z.$35i,w=0;w<I.length;w++){var U=I[w],a=U.parentNode;if(a.$15i===H)return a}},setDragContainment:function(f){this._dragContainment=f},getDragContainment:function(){return this._dragContainment},$20i:function(){var p=this._config,W=p.restoreIconSize||24;return W},$5i:function(u){var C=u.titleIconSize||16;return $&&(C*=1.2),C},$4i:function(r){var O=r.titleHeight||D.widgetTitleHeight;return O},setTitle:function(j,s){if(s==M&&(s=this._config.id),s!=M){var c=this.getPanelConfig(s),C=this.getPanelView(s);c.title=j,C.querySelector(".panel-title span").innerHTML=j}},setInnerPanel:function(Q){var j,$,S=this,v=Q.id,O=S.$35i,_=!1;if(Q.expanded==M&&(Q.expanded=!0),v!=M){var m=S.getPanelConfig(v);if(m){_=!0;var E,U=S.getPanelView(v),g=U.parentNode,X=U.children[0];if(U!==S._view){S.$11i();for(E in m)delete m.key;for(E in Q)m[E]=Q[E];j=S.$24i(Q,g,!1,U),$=j[1],g.removeChild(U);for(var y=0;y<O.length;y++)if(O[y]===X){O.splice(y,1,$);break}S.$12i(),m.expanded=!m.expanded,S.togglePanel(m.id,!0,!0),S.iv()}}}if(!_){S.$11i(),j=S.$24i(Q,S._view.children[1]),$=j[1],O.push($),S._config.items||(S._config.items=[]);var r=S._config.items;if(r.push(Q),S.$12i(),r.length>1){var i=r[r.length-2],T=S.getPanelView(i.id).children[0];x(T,"border-bottom",S.$55i(i))}Q.expanded=!Q.expanded,S.togglePanel(Q.id,!0,!0),S.iv()}},removeInnerPanel:function(P){var Z,u=this,H=-1,U=u._config.items;if(U)for(Z=0;Z<U.length;Z++){var z=U[Z];if(z.id===P){H=Z;break}}if(u.$11i(),H>=0){var Q=u.$35i,i=u.getPanelView(P),j=i.children[0];for(Z=0;Z<Q.length;Z++)if(Q[Z]===j){Q.splice(Z,1);break}U.splice(H,1),i.parentNode.removeChild(i)}if(u.$12i(),U.length>0){var f=U[U.length-1],Y=u.getPanelView(f.id).children[0];x(Y,"border-bottom",u.$55i(f))}},$6i:function(b){x(b,"cursor","pointer"),x(b,"display","inline-block"),x(b,"margin-right",($?8:4)+T),x(b,"vertical-align",G)},$24i:function(F,X,H,q){var f=this,k=f._config.flowLayout,Y=H?X:I(),R=f.$50i(F),s=f.$3i(F,H);Y[v]="ht-widget-panel"+(H?" outer-panel":" inner-panel"),F.borderWidth==M&&(F.borderWidth=H?2:0);var h=F.borderWidth;if(h="0 "+h+T+" "+h+T+" "+h+T+" ",x(Y,"border-width",h),x(Y,"border-color",F.titleBackground||D.panelTitleBackground),x(Y,"border-style","solid"),Q(Y,s),Q(Y,R),H||(q?X.insertBefore(Y,q):Q(X,Y)),!k&&H&&F.minimizable!==!1){var $=g(),r=f.$20i(),P=F.restoreToolTip;O.setCanvas($,r,r),$[v]="control-button button-minimize button-minimize-restore",f.$6i($),x($,"display","none"),Q(X,$),$.title=P||""}var N=F.panelBackground||F.titleBackground||D.panelTitleBackground;if(x(Y,"background-color",N),F.id==M){for(var S=f.$1i++;f.getPanelConfig(S);)S=f.$1i++;F.id=S}return Y.$15i=F.id,F.width&&(Y.style.width=F.width+T),[Y,s,R]},$9i:function(G){var M=g();M[v]="control-button button-toggle button-toggle-expand",M.title=G.toggleToolTip||"";var p=this.$4i(G),m=this.$5i(G);return this.$6i(M),O.setCanvas(M,m,p),M},$8i:function(s){var $=g(),T="control-button button-independent-switch";$[v]=s.independent===!0?T+" button-independent-switch-on":T+" button-independent-switch-off",$.title=s.independentSwitchToolTip||"";var J=this.$4i(s),F=this.$5i(s);return this.$6i($),O.setCanvas($,F,J),$},$7i:function(P){var e=g();e[v]="control-button button-minimize button-minimize-minimize",e.title=P.minimizeToolTip||"";var H=this.$4i(P),Z=this.$5i(P);return this.$6i(e),O.setCanvas(e,Z,H),e},$55i:function(n){var C=this._config,$=C.items,i=n.separatorWidth||D.panelSeparatorWidth,H=n.titleBackground||D.panelTitleBackground,U=n.expanded!==!1?H:n.separatorColor||D.panelSeparatorColor||D.brighter(H);return(C===n||$&&$.indexOf(n)===$.length-1)&&(i=0),i+T+" solid "+U},$3i:function(N,k){var e=this,q=e._config.flowLayout,w=I(),f=I(),W=e.$4i(N),E=N.titleBackground,C=N.titleColor,p=N.titleIcon,J=N.buttons;if(w[v]="panel-title",x(w,L,"relative"),x(w,"background",E||D.panelTitleBackground),x(w,"color",C||D.panelTitleLabelColor),x(w,G,F),x(w,"box-sizing","border-box"),x(w,"-moz-box-sizing","border-box"),x(w,"padding","0 5px 0 0"),x(w,t,"100%"),x(w,"cursor","default"),x(w,"white-space","nowrap"),x(w,"font",D.panelTitleLabelFont),p){var $=g();$[v]="control-button panel-title-icon";var Z=e.$4i(N),M=e.$5i(N);e.$6i($),O.setCanvas($,M,Z),Q(w,$)}var l=c("span");x(l,"display","inline-block"),x(l,"margin-left","5px"),l[Y]="<span>"+N.title+"</span>",Q(w,l),x(w,"line-height",W+T),f[v]="panel-title-controls",x(f,L,K),x(f,S,F),x(f,s,5+T),x(f,G,F),x(f,m,F),x(f,"text-align",s);var u=function(){var B=e.$9i(N);Q(f,B)},b=function(){if(!q&&k&&N.minimizable!==!1){var R=e.$7i(N);Q(f,R)}},h=function(){if(!k){var L=e.$8i(N);Q(f,L)}},H=function(K){var t=g();t[v]="control-button custombutton-"+K.name,t.title=K.toolTip||"",t._action=K.action;var V=e.$4i(N),_=e.$5i(N);e.$6i(t),O.setCanvas(t,_,V),Q(f,t)};if(J)for(var R=0;R<J.length;R++){var o=J[R];"string"==typeof o?"minimize"===o?b():"independentSwitch"===o?h():"toggle"===o&&u():"object"==typeof o&&H(o)}else b(),u();return Q(w,f),w},$50i:function(z){var S=I(),J=z.contentHeight,_=I();x(_,L,"relative"),S[v]="panel-body",x(S,"overflow","hidden");var F=z.contentBackground;if(F===B&&(F="white"),x(S,"background",F),x(S,"font",D.panelContentLabelFont),Q(S,_),z.content){var Z,V=z.content;V.getView?(Q(_,V.getView()),Z=_.children[0]):V instanceof Element?(Q(_,V),Z=_.children[0]):_[Y]=V,V.isSelfViewEvent||Z&&(x(Z,t,"100%"),x(Z,j,"100%")),J&&x(_,j,J+T)}return S},$10i:function(){var D=this,m=D._config,N=D._view,a=N.querySelector(".resize-area").style;a.display=m.flowLayout||m.minimized===!0||m.expanded===!1?W:"block"},$11i:function(){var m=this._view,d=m.children[1];this.$13i>=0?this.$13i++:this.$13i=1,x(d,e,H),x(m,y,H)},$12i:function(){var Z=--this.$13i;if(0===Z){var N=this._view,H=N.children[1];x(H,e,H.scrollHeight+T),x(N,y,N.offsetWidth+T)}},$14i:function(){var b=this._view,o=b.children[0],s=o.children[1].children,j=this._config,O=0;b.$26i=b.offsetWidth,O+=o.children[0].offsetWidth,j.titleIcon&&(O+=o.children[1].offsetWidth,s=o.children[2].children);for(var E=0;E<s.length;E++){var t=s[E];O+=t.offsetWidth+5}b.$51i=O+15},togglePanel:function(B,i,D){function _(n){var b=n.target,h=b.parentNode,X=l.getPanelConfig(h.$15i);delete h.$19i,b!==O&&l.$12i(),l.$18i.fire({kind:"endToggle",target:l,id:X.id})}for(var l=this,I=l._view,O=I.children[1],C=M,E=l.$35i,u=E.length,Z=l._config.exclusive,J=l.$2i,z=[],b=l._config.narrowWhenCollapse,q=0;u>q;q++){var j=E[q],h=j.parentNode,w=h.$15i,c=l.getPanelConfig(w);w===B&&(C=h),!i&&Z&&c.expanded&&h!==I&&w!==B&&c.independent!==!0&&z.push(h)}if(C&&!C.$19i){C.$19i=!0;var r=C.children[1],Y=C.querySelector(".button-toggle"),N=l.getPanelConfig(C.$15i);if(!Y)return;C===I||N.expanded||N.independent===!0||z.forEach(function(a){l.togglePanel(a.$15i,!0)}),C!==I&&l.$11i();var f=200;if(D&&(f=0),l.$18i.fire({kind:"beginToggle",target:l,id:C.$15i}),N.expanded){var K=function(){Y[v]="control-button button-toggle",Y[v]+=J.indexOf("Bottom")>=0?" button-toggle-expand":" button-toggle-collapse",x(r,t,r.clientWidth+T),N.expanded=!1,x(C.children[0],"border-bottom",l.$55i(N)),o(r).duration(f).set("opacity",F).set(e,F).end(_),b&&C===I&&o(C).duration(f).set(y,C.$51i+T).end(),C[v]+=" panel-collapse",o(C).duration(f).set("padding-bottom",F).end(),l.$28i(N,!0),l.$10i()};b&&C===I&&l.$14i(),K()}else Y[v]="control-button button-toggle",Y[v]+=J.indexOf("Bottom")>=0?" button-toggle-collapse":" button-toggle-expand",x(r,t,H),N.expanded=!0,x(C.children[0],"border-bottom",l.$55i(N)),o(r).duration(f).set("opacity","1").set(e,r.scrollHeight+T).end(_),b&&C===I&&o(C).duration(f).set(y,(C.$26i||C.offsetWidth)+T).end(),C[v]=C[v].replace(" panel-collapse",H),o(C).duration(f).end(),l.$28i(N,!0),l.$10i()}},$16i:function(){var N=this._view,p=N.$22i,L=N.$23i,D=this.$2i;return p==M&&(D.indexOf(S)>=0?p=N.$22i=0:D.indexOf(s)>=0&&(p=N.$22i=100)),L==M&&(D.indexOf("Top")>=0?L=N.$23i=0:D.indexOf("Bottom")>=0&&(L=N.$23i=100)),[p,L]},$25i:function(){var Y=this,t=Y._view,C=t.$21i,M=Y.$20i(),O=Y.$16i(),I=O[0],Q=O[1],E=Y.$2i;t.children[0].style.display=W,t.children[1].style.display=W,t.children[2].style.display=H,x(t,"padding",F),x(t,y,M+T),"leftTop"===E?(x(t,S,l(k(t,S))+(C.width-M)*I/100+T),x(t,G,l(k(t,G))+(C.height-M)*Q/100+T)):"leftBottom"===E?(x(t,S,l(k(t,S))+(C.width-M)*I/100+T),x(t,m,l(k(t,m))+(C.height-M)*(1-Q/100)+T)):"rightTop"===E?(x(t,s,l(k(t,s))+(C.width-M)*(1-I/100)+T),x(t,G,l(k(t,G))+(C.height-M)*Q/100+T)):"rightBottom"===E&&(x(t,s,l(k(t,s))+(C.width-M)*(1-I/100)+T),x(t,m,l(k(t,m))+(C.height-M)*(1-Q/100)+T)),t[v]+=" panel-minimized",Y.$18i.fire({kind:"endMinimize",target:Y,id:t.$15i})},$17i:function(){var $=this,A=$._config,z=$._view;x(z,"-webkit-transform",H),x(z,"-ms-transform",H),x(z,"transform",H),A.minimized?$.$25i():($.$18i.fire({kind:"endRestore",target:$,id:A.id}),z[v]=z[v].replace(" panel-minimized",H)),delete z.$19i},minimize:function(k){var y=this,R=y._view;if(!R.$19i&&R.children[0].style.display!==W){var Z=y._config,b=R.getBoundingClientRect(),s=y.$20i(),r=b.width,g=b.height,d=s/r,K=s/g,e=y.$16i(),n=e[0],C=e[1];R.$52i=d,R.$53i=K,R.$21i=b,y.$18i.fire({kind:"beginMinimize",target:y,id:R.$15i});var H=200;k&&(H=0),Z.minimized=!0,R.$19i=!0,Z.expanded&&(R.$26i=R.offsetWidth);var i=n+"% "+C+"%";x(R,"-webkit-transform-origin",i),x(R,"-ms-transform-origin",i),x(R,"transform-origin",i),o(R).duration(H).scale(d,K).end(function(){y.$17i()}),y.$10i()}},restore:function(){var d,F,V,Y,I,K,U,B,t,E,c,n,Q=this,f=Q._view,O=f.parentNode,P=Q._config;if(!f.$19i&&P.minimized){var X=f.$21i,h=f.$52i,w=f.$53i,H=(P.borderWidth+T,Q.$20i()),A=N(O),Z=Q.$2i;"leftTop"===Z?(d=l(k(f,S)),V=l(k(f,G)),I=d,U=V,t=d+X.width-A.width,E=V+X.height-A.height,t>0&&(t>=d?d=0:d-=t),E>0&&(E>=V?V=0:V-=E),c=100*((I-d)/(X.width-H)),n=100*((U-V)/(X.height-H)),x(f,S,d+T),x(f,G,V+T)):"leftBottom"===Z?(d=l(k(f,S)),Y=l(k(f,m)),I=d,B=Y,t=d+X.width-A.width,E=Y+X.height-A.height,t>0&&(t>=d?d=0:d-=t),E>0&&(E>=Y?Y=0:Y-=E),c=100*((I-d)/(X.width-H)),n=100*(1-(B-Y)/(X.height-H)),x(f,S,d+T),x(f,m,Y+T)):"rightTop"===Z?(F=l(k(f,s)),V=l(k(f,G)),K=F,U=V,t=F+X.width-A.width,E=V+X.height-A.height,t>0&&(t>=F?F=0:F-=t),E>0&&(E>=V?V=0:V-=E),c=100*(1-(K-F)/(X.width-H)),n=100*((U-V)/(X.height-H)),x(f,s,F+T),x(f,G,V+T)):"rightBottom"===Z&&(F=l(k(f,s)),Y=l(k(f,m)),K=F,B=Y,t=F+X.width-A.width,E=Y+X.height-A.height,t>0&&(t>=F?F=0:F-=t),E>0&&(E>=Y?Y=0:Y-=E),c=100*(1-(K-F)/(X.width-H)),n=100*(1-(B-Y)/(X.height-H)),x(f,s,F+T),x(f,m,Y+T)),f.children[0].style.display="block",f.children[1].style.display="block",f.children[2].style.display=W,x(f,"-webkit-transform","scale("+h+", "+w+")"),x(f,"-ms-transform","scale("+h+", "+w+")"),x(f,"transform","scale("+h+", "+w+")"),f.$22i=c,f.$23i=n,x(f,"-webkit-transform-origin",c+"% "+n+"%"),x(f,"-ms-transform-origin",c+"% "+n+"%"),x(f,"transform-origin",c+"% "+n+"%"),P.narrowWhenCollapse&&!P.expanded?x(f,y,f.$51i+T):x(f,y,f.$26i+T),Q.$18i.fire({kind:"beginRestore",target:Q,id:f.$15i}),f.$19i=!0,P.minimized=!1,C(function(){o(f).scale(1,1).end(function(){Q.$17i()})},30),Q.$10i()}},addEventListener:function(D,q,a){this.$18i.add(D,q,a)},removeEventListener:function(M,k){this.$18i.remove(M,k)},setPosition:function(R,d){var t=this._view,C=this.$2i;"leftTop"===C?(x(t,S,R+T),x(t,G,d+T),x(t,s,H),x(t,m,H)):"leftBottom"===C?(x(t,S,R+T),x(t,m,d+T),x(t,s,H),x(t,G,H)):"rightTop"===C?(x(t,s,R+T),x(t,G,d+T),x(t,S,H),x(t,m,H)):"rightBottom"===C&&(x(t,s,R+T),x(t,m,d+T),x(t,S,H),x(t,G,H)),delete t.$22i,delete t.$23i},getPosition:function(){var p=this._view,t=this.$2i;return"leftTop"===t?{x:l(k(p,S)),y:l(k(p,G))}:"leftBottom"===t?{x:l(k(p,S)),y:l(k(p,m))}:"rightTop"===t?{x:l(k(p,s)),y:l(k(p,G))}:"rightBottom"===t?{x:l(k(p,s)),y:l(k(p,m))}:void 0},setPositionRelativeTo:function(R){var F=this,B=F._view.querySelectorAll(".button-toggle"),q="control-button button-toggle",U=F.getPosition();F.$2i=R,F.setPosition(U.x,U.y);for(var _=0;_<B.length;_++){var I=B[_],e=F.getPanelConfig(I.parentNode.parentNode.parentNode.$15i);I[v]=e.expanded?R.indexOf("Bottom")>=0?q+" button-toggle-collapse":q+" button-toggle-expand":R.indexOf("Bottom")>=0?q+" button-toggle-expand":q+" button-toggle-collapse"}F.iv()},getPositionRelativeTo:function(){return this.$2i},invalidate:function(L){var u=this;u._68I||(u._68I=1,D.callLater(u.validate,u,M,L),u.onInvalidated&&u.onInvalidated(),u.fireViewEvent("invalidate"));var r=this._config,W=r.content;W&&W.invalidate&&W.invalidate(),r.items&&r.items.forEach(function(X){X&&X.content&&X.content.invalidate&&X.content.invalidate()})},getIconStretch:function(){var Q=this._config.iconStretch||"fill";return Q},$27i:function(b,f,z,y,K){var A=O.initContext(b);O.translateAndScale(A,0,0,1),A.clearRect(0,0,z,z);var I=(z-y)/2;D.drawStretchImage(A,D.getImage(f),this.getIconStretch(K),0,I,y,y),A.restore()},$28i:function(r){var k,j,o,h=this,g=r.id,u=h.getPanelView(g),s=u.querySelector(".button-toggle"),V=h.$2i.indexOf("Bottom")>=0;if(j=V?D.panelCollapseIcon:D.panelExpandIcon,o=V?D.panelExpandIcon:D.panelCollapseIcon,s){k=r.expanded?D.getImage(o):D.getImage(j);var _=h.$4i(r),G=h.$5i(r);h.$27i(s,k,_,G,"toggle")}},$29i:function(e){var f,O=this,V=e.id,z=O.getPanelView(V),X=z.querySelector(".button-independent-switch"),v=D.panelUnLockIcon,j=D.panelLockIcon;if(X){f=e.independent!==!0?D.getImage(j):D.getImage(v);var I=O.$4i(e),W=O.$5i(e);O.$27i(X,f,I,W,"switch")}},$30i:function(Z){var M=this,p=Z.id,T=M.getPanelView(p),z=T.querySelector(".button-minimize-minimize"),m=D.panelMinimizeIcon;if(z){var X=M.$4i(Z),A=M.$5i(Z);M.$27i(z,D.getImage(m),X,A,"miminize")}},$31i:function(C){var W=this,i=C.id,u=W.getPanelView(i),L=u.querySelector(".button-minimize-restore"),x=C.titleIcon||D.panelRestoreIcon;if(L){var s=W.$20i();W.$27i(L,D.getImage(x),s,s,"restore")}},$32i:function(Q){var w=this,u=Q.id,E=w.getPanelView(u);if(Q.buttons){var P=Q.buttons;P.forEach(function(R){var N=R.name,G=R.icon;if(N&&G){var C=E.querySelector(".custombutton-"+N);if(C){var c=w.$4i(Q),k=w.$5i(Q)-1;w.$27i(C,D.getImage(G),c,k,"custom")}}})}},$33i:function(K){var y=this,B=K.id,d=y.getPanelView(B),k=d.querySelector(".panel-title-icon"),h=K.titleIcon;if(k&&h){var M=y.$4i(K),r=y.$5i(K);y.$27i(k,D.getImage(h),M,r,"title")}},validateImpl:function(){var q=this,O=q._config;q.$28i(O),q.$30i(O),q.$31i(O),q.$32i(O),q.$33i(O),O.items&&O.items.forEach(function(u){q.$28i(u),q.$29i(u),q.$32i(u)})}});var P=function(p){var o=this,e=p.getView();o.$34i=p,o.addListeners(),q(e,"dblclick",o.$42i.bind(o))};D.def(P,d,{ms_listener:1,getView:function(){return this.$34i.getView()},clear:function(){delete this.$37i,delete this.$38i,delete this.$36i,delete this.$39i},$42i:function(Y){for(var R=this.$34i,q=Y.target,s=R.$35i,m=s.length,C=0;m>C;C++){var L=s[C];L.contains(q)&&(Y.preventDefault(),R.togglePanel(L.parentNode.$15i))}},handle_touchstart:function(L){var l=this,Z=l.$34i,I=Z._config,z=I.flowLayout,v=L;if(D.isLeftButton(L)){var w=L.target,E=Z.getView().children[0],x=Z.getView().querySelector(".button-minimize-restore");$&&(v=J(L));var h=l.$40i={x:v.pageX,y:v.pageY};l.$41i={x:h.x,y:h.y},(!z&&E.contains(w)||x&&x.contains(w))&&(l.$38i=!0,D.startDragging(l,L)),!z&&l.handle_mousemove(L)&&(l.$37i=!0,D.startDragging(l,L),Z.$11i())}},handle_mousedown:function(n){this.handle_touchstart(n)},handle_touchend:function(W){var L=this,H=L.$34i,j=W.target,I=H.$35i,Z=I.length,R=0,V=H.getView(),P=V.querySelector(".button-minimize"),p=V.querySelector(".button-minimize-restore");if(!L.$39i&&!L.$36i){if(P&&P.contains(j)||p&&p.contains(j))W.preventDefault(),H._config.minimized?H.restore():H.minimize();else for(;Z>R;R++){var N=I[R],t=N.parentNode,_=t.$15i,q=H.getPanelConfig(_),E=N.querySelector(".button-toggle"),y=N.querySelector(".button-independent-switch");if(E===j)W.preventDefault(),H.togglePanel(_);else if(y===j){W.preventDefault();var D="button-independent-switch-off",$="button-independent-switch-on";q.independent=q.independent==M?!0:!q.independent,y[v]=q.independent?y[v].replace(D,$):y[v].replace($,D),H.$29i(q)}else j[v]&&j[v].indexOf("control-button custombutton-")>=0&&N.contains(j)&&j._action.call(H,q,H.getPanelView(_),W)}delete L.$40i,delete L.$41i}},handle_mouseup:function(X){this.handle_touchend(X)},handleWindowTouchEnd:function(){var F=this,i=F.$34i;F.$37i&&F.$36i?(i.$18i.fire({kind:"endResize",target:i,id:i.getView().$15i}),i.$12i()):F.$38i&&F.$39i&&i.$18i.fire({kind:"endMove",target:i,id:i.getView().$15i}),this.clear()},handleWindowMouseUp:function(D){this.handleWindowTouchEnd(D)},handle_mousemove:function(d){var W=this,G=W.getView(),Z=G.querySelector(".resize-area"),p=Z.getBoundingClientRect(),n={x:p.left,y:p.top,width:p.width,height:p.height};d=$?J(d):d;var N=d.clientX,B=d.clientY,O=W.$34i._config;return O.expanded&&O.minimized!==!0&&D.containsPoint(n,{x:N,y:B})?(G.style.cursor="nwse-resize",!0):(G.style.cursor=H,void 0)},handleWindowTouchMove:function(n){n.preventDefault();var c=n;$&&(c=J(n));var o=this,u=o.$40i,v=o.$41i;if(!(v.x==u.x&&v.y==u.y&&D.getDistance(v,{x:c.pageX,y:c.pageY})<=1)){var f=o.$34i,y=o.getView(),L=y.parentNode,X=f._config,h=X.resizeMode||"wh",W=c.pageX-u.x,r=c.pageY-u.y,Q=f.$2i;if(o.$37i){var O=y.children[1].children[0],Z=y.offsetWidth,q=O.offsetHeight,I=Z+W,Y=q+r;I=Math.max(I,100),Y=Math.max(Y,100),"w"===h?(x(y,t,I+T),X.width=I):"h"===h?(x(O,j,Y+T),X.contentHeight=Y):"wh"===h&&(x(y,t,I+T),x(O,j,Y+T),X.width=I,X.contentHeight=Y),Q.indexOf("right")>=0&&x(y,s,l(k(y,s))-(I-Z)+T),Q.indexOf("Bottom")>=0&&x(y,m,l(k(y,m))-(Y-q)+T),u.x=c.pageX,u.y=c.pageY;var R=X.content;R&&R.isSelfViewEvent&&(R.setX(0),R.setY(0),R.setWidth(X.width-2*(X.borderWidth||0)),R.setHeight(X.contentHeight)),o.$36i?f.$18i.fire({kind:"betweenResize",target:f,id:f.getView().$15i}):(o.$36i=!0,f.$18i.fire({kind:"beginResize",target:f,id:f.getView().$15i}))}else if(o.$38i){var A,w,e,U,p,E,B,i,F,C,H=y.getBoundingClientRect(),b=H.width,P=H.height,K=N(L),M=K.width,d=K.height,V=f._dragContainment;"leftTop"===Q?(A=l(k(y,S))||0,e=l(k(y,G))||0,p=A+W,B=e+r,"parent"===V&&(p+b>M&&(p=M-b),B+P>d&&(B=d-P),0>p&&(p=0),0>B&&(B=0)),F=p-A,C=B-e,f.setPosition(p,B),u.x+=F,u.y+=C):"rightBottom"===Q?(w=l(k(y,s))||0,U=l(k(y,m))||0,E=w-W,i=U-r,"parent"===V&&(0>E&&(E=0),0>i&&(i=0),E+b>M&&(E=M-b),i+P>d&&(i=d-P)),F=E-w,C=i-U,f.setPosition(E,i),u.x-=F,u.y-=C):"rightTop"===Q?(w=l(k(y,s))||0,e=l(k(y,G))||0,E=w-W,B=e+r,"parent"===V&&(0>E&&(E=0),0>B&&(B=0),E+b>M&&(E=M-b),B+P>d&&(B=d-P)),F=E-w,C=B-e,f.setPosition(E,B),u.x-=F,u.y+=C):"leftBottom"===Q&&(A=l(k(y,S))||0,U=l(k(y,m))||0,p=A+W,i=U-r,"parent"===V&&(0>p&&(p=0),0>i&&(i=0),p+b>M&&(p=M-b),i+P>d&&(i=d-P)),F=p-A,C=i-U,f.setPosition(p,i),u.x+=F,u.y-=C),o.$39i?f.$18i.fire({kind:"betweenMove",target:f,id:f.getView().$15i}):(o.$39i=!0,f.$18i.fire({kind:"beginMove",target:f,id:f.getView().$15i}))}}},handleWindowMouseMove:function(P){this.handleWindowTouchMove(P)}});var b=i.widget.PanelGroup=function(L){var x=this,I=x._view=O.createView(null,x);I.style.border="1px dashed black",I.style.position="absolute",I.style.background="rgba(120, 120, 120, 0.4)",x.$48i=new i.List,x._tolerance=100,x._config=L||{hGap:0,vGap:0},x.bindHandlePanelMove=x.handlePanelMove.bind(x),x.bindHandlePanelEvent=x.handlePanelEvent.bind(x),x.invalidate()};D.def(b,d,{invalidate:function(){var V=this;V._68I||(V._68I=1,C(function(){V.validate()},50))},validate:function(){if(this._68I){delete this._68I;var $=this.$48i.get(0);if($){var r=$.getView().parentNode;r&&(this.layoutPanels(r,"leftTop"),this.layoutPanels(r,"rightTop"),this.layoutPanels(r,"leftBottom"),this.layoutPanels(r,"rightBottom"))}}},setLeftTopPanels:function(){var d=this,z=d.$43i,I=d.$48i;z==M&&(z=d.$43i=new i.List);for(var R=0;R<arguments.length;R++){var s=arguments[R];if("string"==typeof s)z.$49i=s;else{if(s._config.flowLayout)continue;s.setPositionRelativeTo("leftTop"),z.contains(s)||z.add(s),I.contains(s)||d.add(s)}}},setRightTopPanels:function(){var Y=this,d=Y.$44i,C=Y.$48i;d==M&&(d=Y.$44i=new i.List);for(var K=0;K<arguments.length;K++){var x=arguments[K];if("string"==typeof x)d.$49i=x;else{if(x._config.flowLayout)continue;x.setPositionRelativeTo("rightTop"),d.contains(x)||d.add(x),C.contains(x)||Y.add(x)}}},setLeftBottomPanels:function(){var R=this,Q=R.$45i,K=R.$48i;Q==M&&(Q=R.$45i=new i.List);for(var u=0;u<arguments.length;u++){var a=arguments[u];if("string"==typeof a)Q.$49i=a;else{if(a._config.flowLayout)continue;a.setPositionRelativeTo("leftBottom"),Q.contains(a)||Q.add(a),K.contains(a)||R.add(a)}}},setRightBottomPanels:function(){var I=this,D=I.$46i,q=I.$48i;D==M&&(D=I.$46i=new i.List);for(var J=0;J<arguments.length;J++){var Q=arguments[J];if("string"==typeof Q)D.$49i=Q;else{if(Q._config.flowLayout)continue;Q.setPositionRelativeTo("rightBottom"),D.contains(Q)||D.add(Q),q.contains(Q)||I.add(Q)}}},add:function(W){if(!W._config.flowLayout){var _=this,F=_.$48i;F.contains(W)||(W.addEventListener(_.bindHandlePanelMove),W.addEventListener(_.bindHandlePanelEvent),F.add(W))}},remove:function(P){var n=this,m=n.$48i;m.contains(P)&&(P.removeEventListener(n.bindHandlePanelMove),P.removeEventListener(n.bindHandlePanelEvent),m.remove(P),n.$43i.contains(P)&&n.$43i.remove(P),n.$44i.contains(P)&&n.$44i.remove(P),n.$45i.contains(P)&&n.$45i.remove(P),n.$46i.contains(P)&&n.$46i.remove(P))},layoutPanels:function(k,g,J){var E=this,y=E._config,l=y.hGap||0,a=y.vGap||0;if(k){var n=E.$43i;if("leftBottom"===g?n=E.$45i:"rightTop"===g?n=E.$44i:"rightBottom"===g&&(n=E.$46i),!n)return;var M=n.$49i,r=l,G=a;if(k.contains(E._view)&&k.removeChild(E._view),n&&n.size()>0)for(var s=0;s<n.size();s++){var x=n.get(s),q=x.getView();k=k||q.parentNode,J!==s?x.setPosition(r,G):("leftTop"===g?(E._view.style.right="",E._view.style.bottom="",E._view.style.left=r+T,E._view.style.top=G+T):"leftBottom"===g?(E._view.style.right="",E._view.style.top="",E._view.style.left=r+T,E._view.style.bottom=G+T):"rightTop"===g?(E._view.style.left="",E._view.style.bottom="",E._view.style.right=r+T,E._view.style.top=G+T):"rightBottom"===g&&(E._view.style.left="",E._view.style.top="",E._view.style.right=r+T,E._view.style.bottom=G+T),E._view.style.width=q.offsetWidth+T,E._view.style.height=q.offsetHeight+T,k.insertBefore(E._view,q)),"h"===M?r+=q.offsetWidth+l:"v"===M&&(G+=q.offsetHeight+a)}}},handlePanelEvent:function(f){if("beginToggle"===f.kind||"endToggle"===f.kind||"beginRestore"===f.kind||"endMinimize"===f.kind||"endResize"===f.kind){var N=this,y=f.target,$=y.getView(),b=$.parentNode,K=N.$43i,H=N.$44i,W=N.$45i,k=N.$46i,D=N._config,v=M,l=M,V=y.$47i;if(V==M&&(V=y.$47i=0),"beginToggle"===f.kind?V=y.$47i=V+1:"endToggle"===f.kind&&(V=y.$47i=V-1),K&&K.contains(y)?(v="leftTop",l=K):W&&W.contains(y)?(v="leftBottom",l=W):H&&H.contains(y)?(v="rightTop",l=H):k&&k.contains(y)&&(v="rightBottom",l=k),"beginToggle"===f.kind&&v&&1===V){var e=I(),Z=e.style,B=l.$49i,p="each";Z.fontSize="0",Z.position="absolute",Z.width="100%","leftTop"===v?(Z.left=0,Z.top=0):"leftBottom"===v?(Z.left=0,Z.bottom=0,"v"===B&&(p="reverseEach")):"rightTop"===v?(Z.right=0,Z.top=0,Z.textAlign="right","h"===B&&(p="reverseEach")):"rightBottom"===v&&(Z.right=0,Z.bottom=0,Z.textAlign="right",p="reverseEach"),l[p](function(R){var Y=R.getView(),N=Y.style,k=I();N.position="static",k.style.textAlign="left",k.style.position="relative",k.style.display="inline-block","leftTop"===v?(k.style.marginLeft=D.hGap+T,k.style.marginTop=D.vGap+T):"leftBottom"===v?(k.style.marginLeft=D.hGap+T,k.style.marginBottom=D.vGap+T):"rightTop"===v?(k.style.marginRight=D.hGap+T,k.style.marginTop=D.vGap+T):"rightBottom"===v&&(k.style.marginRight=D.hGap+T,k.style.marginBottom=D.vGap+T),k.appendChild(Y),e.appendChild(k),"h"===B?k.style.verticalAlign="leftTop"===v||"rightTop"===v?"top":"bottom":e.appendChild(c("br"))}),N.$54i=e,b.appendChild(e)}else"endToggle"===f.kind&&v&&0===V?C(function(){b=b.parentNode.parentNode,b.removeChild(N.$54i),delete N.$54i,l.each(function(H){var N=H.getView(),P=N.style;P.position="absolute",b.appendChild(N)}),N.layoutPanels(b,v)},30):("beginRestore"===f.kind||"endMinimize"===f.kind||"endResize"===f.kind)&&v&&N.layoutPanels(b,v)}},handlePanelMove:function(g){if(!(g.kind.indexOf("Move")<0)){var $=this,Y=$._config,_=Y.hGap||0,p=Y.vGap||0,j=g.target,t=j._view,v=t.getBoundingClientRect(),F=v.width,P=v.height,O=F/2,z=P/2,I=t.parentNode,m=$.$43i,k=$.$44i,G=$.$45i,e=$.$46i,Z=I.getBoundingClientRect(),Q=$._tolerance;if("endMove"===g.kind){var u=$._corner;u&&(j.setPositionRelativeTo(u),$.layoutPanels(I,u)),delete $._corner}if("betweenMove"===g.kind){var L=Z.left,W=Z.top,b=Z.width,K=Z.height,s=v.left+F/2,h=v.top+P/2;m==M&&(m=$.$43i=new i.List),G==M&&(G=$.$45i=new i.List),k==M&&(k=$.$44i=new i.List),e==M&&(e=$.$46i=new i.List),delete $._corner,m.contains(j)?(m.remove(j),$.layoutPanels(I,"leftTop")):G.contains(j)?(G.remove(j),$.layoutPanels(I,"leftBottom")):k.contains(j)?(k.remove(j),$.layoutPanels(I,"rightTop")):e.contains(j)&&(e.remove(j),$.layoutPanels(I,"rightBottom"));var r=function(E,X){var H=L+_,V=W+p;if(0===X.size()){var A=H+O,c=V+z;"leftBottom"===E?c=W+K-p-z:"rightTop"===E?A=L+b-_-O:"rightBottom"===E&&(A=L+b-_-O,c=W+K-p-z);var Y=s-A,U=h-c,y=Math.sqrt(Y*Y+U*U);if(Q>y)return $._corner=E,X.add(j),$.layoutPanels(I,E,0),!0}else if(1===X.size()){var T=X.get(0),R=T.getView().getBoundingClientRect(),v=R.left+O,m=R.top+z,D=R.left+R.width+_+O,S=V+z,i=H+O,C=R.top+R.height+p+z;"leftBottom"===E?(m=R.top+R.height-z,S=W+K-p-z,C=R.top-p-z):"rightTop"===E?(v=R.left+R.width-O,D=R.left-_-O,i=L+b-_-O):"rightBottom"===E&&(v=R.left+R.width-O,m=R.top+R.height-z,D=R.left-_-O,S=W+K-p-z,i=L+b-_-O,C=R.top-p-z);var n=s-v,u=h-m,B=s-D,d=h-S,P=s-i,o=h-C,Z=l(Math.sqrt(n*n+u*u)),k=l(Math.sqrt(B*B+d*d)),e=l(Math.sqrt(P*P+o*o)),x=[Z,k,e];x.sort(function(X,H){return X-H});var r=x[0];if(Q>r){if($._corner=E,r===Z)return X.add(j,0),$.layoutPanels(I,E,0),!0;if(r===k)return X.add(j),X.$49i="h",$.layoutPanels(I,E,1),!0;if(r===e)return X.add(j),X.$49i="v",$.layoutPanels(I,E,1),!0}}else if(X.size()>1){for(var f=M,F={},q=[],a=X.$49i,N=0;N<X.size();N++){var t=X.get(N),G=t.getView(),w=G.getBoundingClientRect(),J=w.left+O,g=w.top+z;"leftBottom"===E?g=w.top+w.height-z:"rightTop"===E?J=w.left+w.width-O:"rightBottom"===E&&(J=w.left+w.width-O,g=w.top+w.height-z),N===X.size()-1&&(f=w);var Y=s-J,U=h-g,y=l(Math.sqrt(Y*Y+U*U));F[y]=N,q.push(y)}"leftTop"===E&&"h"===a?(A=f.left+f.width+_+O,c=V+z):"leftTop"===E&&"v"===a?(A=H+O,c=f.top+f.height+p+z):"leftBottom"===E&&"h"===a?(A=f.left+f.width+_+O,c=W+K-p-z):"leftBottom"===E&&"v"===a?(A=H+O,c=f.top-p-z):"rightTop"===E&&"h"===a?(A=f.left-_-O,c=V+z):"rightTop"===E&&"v"===a?(A=L+b-_-O,c=f.top+f.height+p+z):"rightBottom"===E&&"h"===a?(A=f.left-_-O,c=W+K-p-z):"rightBottom"===E&&"v"===a&&(A=L+b-_-O,c=f.top-p-z),Y=s-A,U=h-c,y=l(Math.sqrt(Y*Y+U*U)),F[y]=N,q.push(y),q.sort(function(I,D){return I-D});var r=q[0];if(Q>r)return $._corner=E,X.add(j,F[r]),$.layoutPanels(I,E,F[r]),!0}};r("leftTop",m)||r("leftBottom",G)||r("rightTop",k)||r("rightBottom",e)}}}})}("undefined"!=typeof global?global:"undefined"!=typeof self?self:"undefined"!=typeof window?window:(0,eval)("this"),Object);