!function(W){"use strict";var M="ht",F=W[M],c=Math,Y=c.max,s=c.min,E=c.abs,D=c.atan2,d=(c.cos,c.sin,c.ceil),I=F.Default,i=I.getInternal(),z=F.List,n=i.Mat,Z=i.getNodeRect,S=i.intersectionLineRect,X=I.getDistance,o=I.setEdgeType,h="left",v="right",H="top",e="bottom",U="edge.type",V="edge.gap",r="edge.center",t="edge.extend",R=function(i,J){return Z(i,J).width},T=function(D,U){return Z(D,U).height},g=function(H,Q){return i.getEdgeHostPosition(H,Q,"source")},J=function(Z,u){return i.getEdgeHostPosition(Z,u,"target")},j=function(l,W){var m=l.s(U),s=l.getEdgeGroup();if(s){var z=0;if(s.eachSiblingEdge(function(Y){W.isVisible(Y)&&Y.s(U)==m&&z++}),z>1)return l.s(V)*(z-1)/2}return 0},B=function(B,s){var r=B.s(U),v=B.isLooped();if(!B.getEdgeGroup())return v?B.s(V):0;var Q,e=0,c=0,Y=0;return B.getEdgeGroup().getSiblings().each(function(j){j.each(function($){if($._40I===B._40I&&$.s(U)==r&&s.isVisible($)){var b=$.s(V);Q?(c+=Y/2+b/2,Y=b):(Q=$,Y=b),$===B&&(e=c)}})}),v?c-e+Y:e-c/2},Q=function(i,Y){var W=Y.s("edge.corner.radius");return I.toRoundedCorner(i,W)};i.addMethod(F.Style,{"edge.ripple.elevation":-20,"edge.ripple.size":1,"edge.ripple.both":!1,"edge.ripple.straight":!1,"edge.ripple.length":-1,"edge.corner.radius":-1,"edge.ortho":.5,"edge.flex":20,"edge.extend":20},!0),o("boundary",function(e,q,t,E){E||(q=-q);var p,d=g(t,e),a=J(t,e),j=Z(t,e._40I),Q=Z(t,e._41I),i=new n(D(a.y-d.y,a.x-d.x)),M=X(d,a),B=d.x,l=d.y;return p=i.tf(0,q),d={x:p.x+B,y:p.y+l},p=i.tf(M,q),a={x:p.x+B,y:p.y+l},p=S(d,a,j),p&&(d={x:p[0],y:p[1]}),p=S(d,a,Q),p&&(a={x:p[0],y:p[1]}),{points:new z([d,a])}}),o("ripple",function(l,q,m,M){M||(q=-q);var Y=g(m,l),p=J(m,l),h=X(Y,p),V=s(l.s("edge.offset"),h/2),e=l.s("edge.ripple.size"),B=l.s("edge.ripple.length"),a=l.s("edge.ripple.both"),T=l.s(r),v=l.s("edge.ripple.elevation"),H=new z,o=l.s("edge.ripple.straight")?null:new z,W=new n(D(p.y-Y.y,p.x-Y.x));M||(v=-v),h-=2*V,B>0&&(e=d(h/B));var S=h/e;o&&o.add(1);for(var O=0;e>O;O++)o&&o.add(3),0===O?H.add({x:V+S*O,y:T?0:q}):H.add({x:V+S*O,y:q}),H.add({x:V+S*O+S/2,y:v+q}),a&&(v=-v);for(H.add({x:V+h,y:T?0:q}),O=0;O<H.size();O++){var u=W.tf(H.get(O));u.x+=Y.x,u.y+=Y.y,H.set(O,u)}return{points:H,segments:o}}),o("h.v",function(d,q,s){q=B(d,s);var o=new z,m=d.s(r),x=g(s,d),f=x.x,X=x.y,K=J(s,d),Y=K.x,C=K.y,V=d._40I instanceof F.Edge,D=d._41I instanceof F.Edge,L=0,n=0,$=Y-f,u=C-X;return m||(L=V?0:R(s,d._40I)/2,n=D?0:T(s,d._41I)/2),$>=0&&0>=u?(o.add({x:f+L,y:X+q}),o.add({x:Y+q,y:X+q}),o.add({x:Y+q,y:C+n})):0>=$&&u>=0?(o.add({x:f-L,y:X+q}),o.add({x:Y+q,y:X+q}),o.add({x:Y+q,y:C-n})):$>=0&&u>=0?(o.add({x:f+L,y:X+q}),o.add({x:Y-q,y:X+q}),o.add({x:Y-q,y:C-n})):(o.add({x:f-L,y:X+q}),o.add({x:Y-q,y:X+q}),o.add({x:Y-q,y:C+n})),Q(o,d)}),o("v.h",function(_,s,X){s=B(_,X);var H=new z,d=_.s(r),U=g(X,_),b=U.x,p=U.y,k=J(X,_),W=k.x,V=k.y,E=_._40I instanceof F.Edge,n=_._41I instanceof F.Edge,Y=0,c=0,C=W-b,G=V-p;return d||(Y=n?0:R(X,_._41I)/2,c=E?0:T(X,_._40I)/2),C>=0&&0>=G?(H.add({x:b+s,y:p-c}),H.add({x:b+s,y:V+s}),H.add({x:W-Y,y:V+s})):0>=C&&G>=0?(H.add({x:b+s,y:p+c}),H.add({x:b+s,y:V+s}),H.add({x:W+Y,y:V+s})):C>=0&&G>=0?(H.add({x:b-s,y:p+c}),H.add({x:b-s,y:V+s}),H.add({x:W-Y,y:V+s})):(H.add({x:b-s,y:p-c}),H.add({x:b-s,y:V+s}),H.add({x:W+Y,y:V+s})),Q(H,_)}),o("ortho",function(A,O,Y){var d=new z,x=A.s(r),j=A.s("edge.ortho"),h=A._40I,B=A._41I,Z=g(Y,A),H=Z.x,W=Z.y,C=J(Y,A),v=C.x,M=C.y,s=v-H,n=M-W,l=h instanceof F.Edge,k=B instanceof F.Edge,f=x||l?0:R(Y,h)/2,p=x||l?0:T(Y,h)/2,D=x||k?0:R(Y,B)/2,U=x||k?0:T(Y,B)/2,y=(s-(f+D)*(s>0?1:-1))*j,u=(n-(p+U)*(n>0?1:-1))*j;return E(s)<E(n)?s>=0&&0>=n?(d.add({x:H+O,y:W-p}),d.add({x:H+O,y:W+u+O-p}),d.add({x:v+O,y:W+u+O-p}),d.add({x:v+O,y:M+U})):0>=s&&n>=0?(d.add({x:H+O,y:W+p}),d.add({x:H+O,y:W+u+O+p}),d.add({x:v+O,y:W+u+O+p}),d.add({x:v+O,y:M-U})):s>=0&&n>=0?(d.add({x:H+O,y:W+p}),d.add({x:H+O,y:W+u-O+p}),d.add({x:v+O,y:W+u-O+p}),d.add({x:v+O,y:M-U})):(d.add({x:H+O,y:W-p}),d.add({x:H+O,y:W+u-O-p}),d.add({x:v+O,y:W+u-O-p}),d.add({x:v+O,y:M+U})):s>=0&&0>=n?(d.add({x:H+f,y:W+O}),d.add({x:H+y+O+f,y:W+O}),d.add({x:H+y+O+f,y:M+O}),d.add({x:v-D,y:M+O})):0>=s&&n>=0?(d.add({x:H-f,y:W+O}),d.add({x:H+y+O-f,y:W+O}),d.add({x:H+y+O-f,y:M+O}),d.add({x:v+D,y:M+O})):s>=0&&n>=0?(d.add({x:H+f,y:W+O}),d.add({x:H+y-O+f,y:W+O}),d.add({x:H+y-O+f,y:M+O}),d.add({x:v-D,y:M+O})):(d.add({x:H-f,y:W+O}),d.add({x:H+y-O-f,y:W+O}),d.add({x:H+y-O-f,y:M+O}),d.add({x:v+D,y:M+O})),Q(d,A)}),o("flex",function(A,X,Z){var d=new z,x=A.s("edge.flex")+j(A,Z),b=A.s(r),i=A._40I,m=A._41I,U=g(Z,A),V=U.x,K=U.y,I=J(Z,A),Y=I.x,c=I.y,o=i instanceof F.Edge,y=m instanceof F.Edge,W=Y-V,s=c-K,p=b||o?0:R(Z,i)/2,l=b||o?0:T(Z,i)/2,f=b||y?0:R(Z,m)/2,D=b||y?0:T(Z,m)/2,M=W>0?x:-x,H=s>0?x:-x;return E(W)<E(s)?W>=0&&0>=s?(d.add({x:V+X,y:K-l}),d.add({x:V+X,y:K+H+X-l}),d.add({x:Y+X,y:c-H+X+D}),d.add({x:Y+X,y:c+D})):0>=W&&s>=0?(d.add({x:V+X,y:K+l}),d.add({x:V+X,y:K+H+X+l}),d.add({x:Y+X,y:c-H+X-D}),d.add({x:Y+X,y:c-D})):W>=0&&s>=0?(d.add({x:V+X,y:K+l}),d.add({x:V+X,y:K+H-X+l}),d.add({x:Y+X,y:c-H-X-D}),d.add({x:Y+X,y:c-D})):(d.add({x:V+X,y:K-l}),d.add({x:V+X,y:K+H-X-l}),d.add({x:Y+X,y:c-H-X+D}),d.add({x:Y+X,y:c+D})):W>=0&&0>=s?(d.add({x:V+p,y:K+X}),d.add({x:V+M+X+p,y:K+X}),d.add({x:Y-M+X-f,y:c+X}),d.add({x:Y-f,y:c+X})):0>=W&&s>=0?(d.add({x:V-p,y:K+X}),d.add({x:V+M+X-p,y:K+X}),d.add({x:Y-M+X+f,y:c+X}),d.add({x:Y+f,y:c+X})):W>=0&&s>=0?(d.add({x:V+p,y:K+X}),d.add({x:V+M-X+p,y:K+X}),d.add({x:Y-M-X-f,y:c+X}),d.add({x:Y-f,y:c+X})):(d.add({x:V-p,y:K+X}),d.add({x:V+M-X-p,y:K+X}),d.add({x:Y-M-X+f,y:c+X}),d.add({x:Y+f,y:c+X})),Q(d,A)}),o("extend.east",function(e,W,G){var T=new z,K=e.s(t)+j(e,G),I=e.s(r),D=g(G,e),c=e._40I instanceof F.Edge,C=e._41I instanceof F.Edge,u=D.x+(I||c?0:R(G,e._40I)/2),k=D.y,B=J(G,e),m=B.x+(I||C?0:R(G,e._41I)/2),q=B.y,v=Y(u,m)+K;return k>q?(T.add({x:u,y:k+W}),T.add({x:v+W,y:k+W}),T.add({x:v+W,y:q-W}),T.add({x:m,y:q-W})):(T.add({x:u,y:k-W}),T.add({x:v+W,y:k-W}),T.add({x:v+W,y:q+W}),T.add({x:m,y:q+W})),Q(T,e)}),o("extend.west",function(M,N,v){var o=new z,e=M.s(t)+j(M,v),D=M.s(r),k=M._40I instanceof F.Edge,X=M._41I instanceof F.Edge,Y=g(v,M),p=Y.x-(D||k?0:R(v,M._40I)/2),l=Y.y,O=J(v,M),E=O.x-(D||X?0:R(v,M._41I)/2),a=O.y,U=s(p,E)-e;return l>a?(o.add({x:p,y:l+N}),o.add({x:U-N,y:l+N}),o.add({x:U-N,y:a-N}),o.add({x:E,y:a-N})):(o.add({x:p,y:l-N}),o.add({x:U-N,y:l-N}),o.add({x:U-N,y:a+N}),o.add({x:E,y:a+N})),Q(o,M)}),o("extend.north",function(o,M,X){var c=new z,f=o.s(t)+j(o,X),L=o.s(r),C=o._40I instanceof F.Edge,O=o._41I instanceof F.Edge,k=g(X,o),G=k.x,$=k.y-(L||C?0:T(X,o._40I)/2),D=J(X,o),d=D.x,V=D.y-(L||O?0:T(X,o._41I)/2),l=s($,V)-f;return G>d?(c.add({y:$,x:G+M}),c.add({y:l-M,x:G+M}),c.add({y:l-M,x:d-M}),c.add({y:V,x:d-M})):(c.add({y:$,x:G-M}),c.add({y:l-M,x:G-M}),c.add({y:l-M,x:d+M}),c.add({y:V,x:d+M})),Q(c,o)}),o("extend.south",function(o,i,W){var h=new z,_=o.s(t)+j(o,W),m=o.s(r),K=o._40I instanceof F.Edge,x=o._41I instanceof F.Edge,L=g(W,o),e=L.x,s=L.y+(m||K?0:T(W,o._40I)/2),R=J(W,o),Z=R.x,n=R.y+(m||x?0:T(W,o._41I)/2),V=Y(s,n)+_;return e>Z?(h.add({y:s,x:e+i}),h.add({y:V+i,x:e+i}),h.add({y:V+i,x:Z-i}),h.add({y:n,x:Z-i})):(h.add({y:s,x:e-i}),h.add({y:V+i,x:e-i}),h.add({y:V+i,x:Z+i}),h.add({y:n,x:Z+i})),Q(h,o)});var w=function(n,T,S,d,Z){if(d.sort(function(D,Y){var d=D.getSourceAgent()===T?D.getTargetAgent():D.getSourceAgent(),l=Y.getSourceAgent()===T?Y.getTargetAgent():Y.getSourceAgent(),m=d.p(),W=l.p();if(S===h||S===v){if(m.y>W.y)return 1;if(m.y<W.y)return-1}else{if(m.x>W.x)return 1;if(m.x<W.x)return-1}return I.sortFunc(D.getId(),Y.getId())}),Z){for(var D,u,i,J=n.getSourceAgent(),t=n.getTargetAgent(),R=0;R<d.size();R++){var M=d.get(R);M.getSourceAgent()===J&&M.getTargetAgent()===t||M.getTargetAgent()===J&&M.getSourceAgent()===t?(u||(u=new z),u.add(M,0)):u?(i||(i=new z),i.add(M)):(D||(D=new z),D.add(M))}d.clear(),D&&d.addAll(D),u&&d.addAll(u),i&&d.addAll(i)}var L=d.indexOf(n),x=d.size(),O=n.s(V);return{side:S,index:L,size:x,offset:-O*(x-1)/2+O*L}},m=function(b,M,Q,d,j){var p=M.s(U);return w(M,Q,d,Q.getAgentEdges().toList(function(S){return b.isVisible(S)&&S.s(U)===p}),j)},A=function(C,j){var y=C.getSourceAgent()===j?C.getTargetAgent():C.getSourceAgent(),N=j.p(),D=y.p(),l=D.x-N.x,T=D.y-N.y;return l>0&&E(T)<=l?v:0>l&&E(T)<=-l?h:T>0&&E(l)<=T?e:H},L=function(x,R,V){var f=R.s(U),W=A(R,V);return w(R,V,W,V.getAgentEdges().toList(function(Y){return x.isVisible(Y)&&Y.s(U)===f&&A(Y,V)===W}))},p=function(O,d){var C=O.getSourceAgent()===d,F=C?O.getTargetAgent():O.getSourceAgent(),M=d.p(),o=F.p();return C?M.y>o.y?H:e:M.x<o.x?v:h},N=function(W,X,$){var d=X.s(U),o=p(X,$);return w(X,$,o,$.getAgentEdges().toList(function(n){return W.isVisible(n)&&n.s(U)===d&&p(n,$)===o}),o===v||o===e)},l=function(u,X){var Z=u.getSourceAgent()===X,I=Z?u.getTargetAgent():u.getSourceAgent(),A=X.p(),M=I.p();return Z?A.x<M.x?v:h:A.y>M.y?H:e},y=function(s,h,c){var T=h.s(U),G=l(h,c);return w(h,c,G,c.getAgentEdges().toList(function(P){return s.isVisible(P)&&P.s(U)===T&&l(P,c)===G}),G===v||G===e)},K=function(y,L,n){var q=y.getSourceAgent(),g=y.getTargetAgent(),B=q.getId()>g.getId(),i=B?g:q,w=B?q:g,E=i.p(),J=w.p(),M=n(L,y,i),u=n(L,y,w),Z=y.s(r),s=Z?0:R(L,i)/2,p=Z?0:R(L,w)/2,b=Z?0:T(L,i)/2,t=Z?0:T(L,w)/2,A=M.offset,N=u.offset,c=M.side,x=u.side,_=new z;return c===H?(_.add({x:E.x+A,y:E.y-b}),_.add({x:E.x+A,y:J.y+N}),x===h?_.add({x:J.x-p,y:J.y+N}):_.add({x:J.x+p,y:J.y+N})):c===e?(_.add({x:E.x+A,y:E.y+b}),_.add({x:E.x+A,y:J.y+N}),x===h?_.add({x:J.x-p,y:J.y+N}):_.add({x:J.x+p,y:J.y+N})):c===h?(_.add({x:E.x-s,y:E.y+A}),_.add({x:J.x+N,y:E.y+A}),x===e?_.add({x:J.x+N,y:J.y+t}):_.add({x:J.x+N,y:J.y-t})):c===v&&(_.add({x:E.x+s,y:E.y+A}),_.add({x:J.x+N,y:E.y+A}),x===e?_.add({x:J.x+N,y:J.y+t}):_.add({x:J.x+N,y:J.y-t})),B&&_.reverse(),Q(_,y)};o("ortho2",function(W,y,w){var j,V,F=W.s(r),s=W.s("edge.ortho"),U=W.getSourceAgent(),i=W.getTargetAgent(),Z=U.getId()>i.getId(),d=Z?i:U,p=Z?U:i,n=d.p(),J=p.p(),I=L(w,W,d),c=L(w,W,p),K=F?0:R(w,d)/2,D=F?0:T(w,d)/2,S=F?0:R(w,p)/2,G=F?0:T(w,p)/2,P=new z,g=I.offset,Y=c.offset,a=I.side;return a===v?(j=J.y>n.y?-g:g,V=n.x+K+(J.x-S-n.x-K)*s,P.add({x:n.x+K,y:n.y+g}),P.add({x:V+j,y:n.y+g}),P.add({x:V+j,y:J.y+Y}),P.add({x:J.x-S,y:J.y+Y})):a===h?(j=J.y>n.y?-g:g,V=n.x-K-(n.x-K-J.x-S)*s,P.add({x:n.x-K,y:n.y+g}),P.add({x:V-j,y:n.y+g}),P.add({x:V-j,y:J.y+Y}),P.add({x:J.x+S,y:J.y+Y})):a===e?(j=J.x>n.x?-g:g,V=n.y+D+(J.y-G-n.y-D)*s,P.add({x:n.x+g,y:n.y+D}),P.add({x:n.x+g,y:V+j}),P.add({x:J.x+Y,y:V+j}),P.add({x:J.x+Y,y:J.y-G})):a===H&&(j=J.x>n.x?-g:g,V=n.y-D-(n.y-D-J.y-G)*s,P.add({x:n.x+g,y:n.y-D}),P.add({x:n.x+g,y:V-j}),P.add({x:J.x+Y,y:V-j}),P.add({x:J.x+Y,y:J.y+G})),Z&&P.reverse(),Q(P,W)},!0),o("flex2",function(_,c,F){var d,E=_.getSourceAgent(),U=_.getTargetAgent(),p=E.getId()>U.getId(),m=p?U:E,C=p?E:U,o=m.p(),G=C.p(),D=L(F,_,m),Y=L(F,_,C),t=_.s(r),l=_.s("edge.flex")+(D.size-1)/2*_.s(V),g=t?0:R(F,m)/2,s=t?0:T(F,m)/2,W=t?0:R(F,C)/2,B=t?0:T(F,C)/2,M=new z,I=D.offset,j=Y.offset,u=D.side;return u===v?(d=G.y>o.y?-I:I,M.add({x:o.x+g,y:o.y+I}),M.add({x:o.x+g+l+d,y:o.y+I}),M.add({x:G.x-W-l+d,y:G.y+j}),M.add({x:G.x-W,y:G.y+j})):u===h?(d=G.y>o.y?-I:I,M.add({x:o.x-g,y:o.y+I}),M.add({x:o.x-g-l-d,y:o.y+I}),M.add({x:G.x+W+l-d,y:G.y+j}),M.add({x:G.x+W,y:G.y+j})):u===e?(d=G.x>o.x?-I:I,M.add({x:o.x+I,y:o.y+s}),M.add({x:o.x+I,y:o.y+s+l+d}),M.add({x:G.x+j,y:G.y-B-l+d}),M.add({x:G.x+j,y:G.y-B})):u===H&&(d=G.x>o.x?-I:I,M.add({x:o.x+I,y:o.y-s}),M.add({x:o.x+I,y:o.y-s-l-d}),M.add({x:G.x+j,y:G.y+B+l-d}),M.add({x:G.x+j,y:G.y+B})),p&&M.reverse(),Q(M,_)},!0),o("extend.north2",function(G,N,f){var S=G.getSourceAgent(),M=G.getTargetAgent(),F=S.getId()>M.getId(),w=F?M:S,v=F?S:M,o=w.p(),J=v.p(),u=m(f,G,w,H),g=m(f,G,v,H,!0),n=G.s(r),B=n?0:T(f,w)/2,X=n?0:T(f,v)/2,b=u.offset,j=g.offset,y=G.s(t)+(u.size-1)/2*G.s(V),O=s(o.y-B,J.y-X)-y+(o.x<J.x?b:-b),e=new z;return e.add({x:o.x+b,y:o.y-B}),e.add({x:o.x+b,y:O}),e.add({x:J.x+j,y:O}),e.add({x:J.x+j,y:J.y-X}),F&&e.reverse(),Q(e,G)},!0),o("extend.south2",function(a,p,P){var w=a.getSourceAgent(),f=a.getTargetAgent(),M=w.getId()>f.getId(),J=M?f:w,h=M?w:f,g=J.p(),S=h.p(),A=m(P,a,J,e),x=m(P,a,h,e,!0),$=a.s(r),K=$?0:T(P,J)/2,l=$?0:T(P,h)/2,B=A.offset,d=x.offset,c=a.s(t)+(A.size-1)/2*a.s(V),O=Y(g.y+K,S.y+l)+c+(g.x>S.x?B:-B),W=new z;return W.add({x:g.x+B,y:g.y+K}),W.add({x:g.x+B,y:O}),W.add({x:S.x+d,y:O}),W.add({x:S.x+d,y:S.y+l}),M&&W.reverse(),Q(W,a)},!0),o("extend.west2",function(J,I,T){var U=J.getSourceAgent(),P=J.getTargetAgent(),A=U.getId()>P.getId(),q=A?P:U,b=A?U:P,Y=q.p(),M=b.p(),u=m(T,J,q,H),f=m(T,J,b,H,!0),k=J.s(r),G=k?0:R(T,q)/2,i=k?0:R(T,b)/2,p=u.offset,W=f.offset,$=J.s(t)+(u.size-1)/2*J.s(V),d=s(Y.x-G,M.x-i)-$+(Y.y<M.y?p:-p),X=new z;return X.add({x:Y.x-G,y:Y.y+p}),X.add({x:d,y:Y.y+p}),X.add({x:d,y:M.y+W}),X.add({x:M.x-i,y:M.y+W}),A&&X.reverse(),Q(X,J)},!0),o("extend.east2",function(h,B,O){var L=h.getSourceAgent(),C=h.getTargetAgent(),l=L.getId()>C.getId(),D=l?C:L,s=l?L:C,J=D.p(),y=s.p(),P=m(O,h,D,H),G=m(O,h,s,H,!0),e=h.s(r),u=e?0:R(O,D)/2,M=e?0:R(O,s)/2,K=P.offset,N=G.offset,x=h.s(t)+(P.size-1)/2*h.s(V),b=Y(J.x+u,y.x+M)+x+(J.y>y.y?K:-K),F=new z;return F.add({x:J.x+u,y:J.y+K}),F.add({x:b,y:J.y+K}),F.add({x:b,y:y.y+N}),F.add({x:y.x+M,y:y.y+N}),l&&F.reverse(),Q(F,h)},!0),o("v.h2",function(t,W,$){return K(t,$,N)},!0),o("h.v2",function(p,k,l){return K(p,l,y)},!0)}("undefined"!=typeof global?global:"undefined"!=typeof self?self:"undefined"!=typeof window?window:(0,eval)("this"),Object);