!function(f,d){"use strict";var e="ht",D=e+".graph.",u=f[e],j=u.graph,C=u.Default,g=u.Color,v=null,A="px",U=C.getInternal(),o=U.getPinchDist,X=C.preventDefault,R=C.getTouchCount,T=C.startDragging;U.addMethod(C,{overviewBackground:g.widgetBackground,overviewMaskBackground:g.transparent,overviewContentBorderColor:g.widgetBorder,overviewContentBackground:g.background},!0),j.Overview=function(T){var S=this,w=S._view=U.createView(1,S);S._canvas=U.createCanvas(w),w.style.background=C.overviewBackground,w.appendChild(S._mask=U.createDiv()),S.setGraphView(T),S.addListeners()},C.def(D+"Overview",d,{ms_v:1,ms_fire:1,ms_listener:1,ms_ac:["maskBackground","contentBorderColor","contentBackground","autoUpdate","fixToRect"],_autoUpdate:!0,_fixToRect:!1,_rate:1,_scrollRect:{x:0,y:0,width:0,height:0},_maskBackground:C.overviewMaskBackground,_contentBorderColor:C.overviewContentBorderColor,_contentBackground:C.overviewContentBackground,getGraphView:function(){return this.gv},setGraphView:function(b){var $=this;if($.gv!==b){var j=$.gv;$.gv=b,j&&(j.removeViewListener($.handleGraphViewChanged,$),j.ump($.handleGraphViewPropertyChanged,$)),b&&(b.addViewListener($.handleGraphViewChanged,$),b.mp($.handleGraphViewPropertyChanged,$)),$.fp("graphView",j,b),$.redraw()}},getCanvas:function(){return this._canvas},getMask:function(){return this._mask},dispose:function(){this.setGraphView(null)},onPropertyChanged:function(){this.redraw()},handleGraphViewChanged:function($){this._autoUpdate&&"validate"===$.kind&&this.redraw()},handleGraphViewPropertyChanged:function(D){("canvasBackground"===D.property||this.getFixToRect()&&("zoom"===D.property||"translateX"===D.property||"translateY"===D.property))&&this.redraw()},redraw:function(){var y=this;y._redraw||(y._redraw=1,y.iv(50))},validateImpl:function(){var I=this,t=I.gv,T=I._canvas,F=I.getWidth(),l=I.getHeight(),h=I._redraw;if(t){var Y=I._mask,g=Y.style,G=t.getViewRect(),V=this.getFixToRect(),b=V?"boolean"==typeof V?t.getContentRect():V:t.getScrollRect(),R=b.x,O=b.y,e=b.width,N=b.height,f=I._rate=Math.max(e/F,N/l),P=I._x=(F-e/f)/2,X=I._y=(l-N/f)/2;if(0!==G.width&&0!==G.height||I.hasRetry||(C.callLater(I.iv,I,v),I.hasRetry=!0),(F!==T.clientWidth||l!==T.clientHeight)&&(U.setCanvas(T,F,l),h=1),U.isSameRect(b,I._scrollRect)||(I._scrollRect=b,h=1),h){var z=U.initContext(T),B=t.getDataModel(),i=B.getBackground()||I._contentBackground,d=I._contentBorderColor,H=C.devicePixelRatio;z.clearRect(0,0,F*H,l*H);var $=B.getBackground()&&B.a("width")>0&&B.a("height")>0;i&&!$&&U.fillRect(z,P*H,X*H,e/f*H,N/f*H,i),U.translateAndScale(z,-R/f+P,-O/f+X,1/f),t._42(z),z.restore(),d&&U.drawBorder(z,d,P*H,X*H,e/f*H,N/f*H)}g.background=I._maskBackground,g.left=P+(G.x-R)/f+A,g.top=X+(G.y-O)/f+A,g.width=G.width/f+A,g.height=G.height/f+A,I._redraw=null}else if(h){var z=U.initContext(T);z.clearRect(0,0,F,l),z.restore(),I._redraw=null}},center:function(R){var x=this,H=x.gv;if(H){var N=H._zoom,W=H._29I,A=x._rate,O=x._scrollRect,K=C.getLogicalPoint(R,x._canvas),Q=O.x+(K.x-x._x)*A,u=O.y+(K.y-x._y)*A;H.setTranslate((W.width/2-Q)*N,(W.height/2-u)*N)}},handle_mousedown:function(p){this.handle_touchstart(p)},handleWindowMouseUp:function(e){this.handleWindowTouchEnd(e)},handleWindowMouseMove:function(F){this.handleWindowTouchMove(F)},handle_mousewheel:function(P){this.handleScroll(P,P.wheelDelta)},handle_DOMMouseScroll:function(k){2===k.axis&&this.handleScroll(k,-k.detail)},handleScroll:function(R,r){if(X(R),this.gv){var q=this.gv;r>0?q.scrollZoomIn():0>r&&q.scrollZoomOut()}},handle_touchstart:function(H){if(X(H),this.gv&&C.isLeftButton(H)){var U=this,_=U.gv,Y=R(H);1===Y?C.isDoubleClick(H)&&_.isResettable()?_.reset():(U.center(H),T(U,H)):2===Y&&(U._dist=o(H),T(U,H))}},handleWindowTouchEnd:function(){delete this._dist},handleWindowTouchMove:function(T){if(this.gv){var J=this,s=J._dist,d=R(T);1===d?J.center(T):2===d&&s!=v&&J.gv.handlePinch(v,o(T),s)}}})}("undefined"!=typeof global?global:"undefined"!=typeof self?self:"undefined"!=typeof window?window:(0,eval)("this"),Object);