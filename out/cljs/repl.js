// Compiled by ClojureScript 1.8.51 {}
goog.provide('cljs.repl');
goog.require('cljs.core');
cljs.repl.print_doc = (function cljs$repl$print_doc(m){
cljs.core.println.call(null,"-------------------------");

cljs.core.println.call(null,[cljs.core.str((function (){var temp__4657__auto__ = new cljs.core.Keyword(null,"ns","ns",441598760).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(temp__4657__auto__)){
var ns = temp__4657__auto__;
return [cljs.core.str(ns),cljs.core.str("/")].join('');
} else {
return null;
}
})()),cljs.core.str(new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(m))].join(''));

if(cljs.core.truth_(new cljs.core.Keyword(null,"protocol","protocol",652470118).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"Protocol");
} else {
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"forms","forms",2045992350).cljs$core$IFn$_invoke$arity$1(m))){
var seq__11922_11936 = cljs.core.seq.call(null,new cljs.core.Keyword(null,"forms","forms",2045992350).cljs$core$IFn$_invoke$arity$1(m));
var chunk__11923_11937 = null;
var count__11924_11938 = (0);
var i__11925_11939 = (0);
while(true){
if((i__11925_11939 < count__11924_11938)){
var f_11940 = cljs.core._nth.call(null,chunk__11923_11937,i__11925_11939);
cljs.core.println.call(null,"  ",f_11940);

var G__11941 = seq__11922_11936;
var G__11942 = chunk__11923_11937;
var G__11943 = count__11924_11938;
var G__11944 = (i__11925_11939 + (1));
seq__11922_11936 = G__11941;
chunk__11923_11937 = G__11942;
count__11924_11938 = G__11943;
i__11925_11939 = G__11944;
continue;
} else {
var temp__4657__auto___11945 = cljs.core.seq.call(null,seq__11922_11936);
if(temp__4657__auto___11945){
var seq__11922_11946__$1 = temp__4657__auto___11945;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__11922_11946__$1)){
var c__11563__auto___11947 = cljs.core.chunk_first.call(null,seq__11922_11946__$1);
var G__11948 = cljs.core.chunk_rest.call(null,seq__11922_11946__$1);
var G__11949 = c__11563__auto___11947;
var G__11950 = cljs.core.count.call(null,c__11563__auto___11947);
var G__11951 = (0);
seq__11922_11936 = G__11948;
chunk__11923_11937 = G__11949;
count__11924_11938 = G__11950;
i__11925_11939 = G__11951;
continue;
} else {
var f_11952 = cljs.core.first.call(null,seq__11922_11946__$1);
cljs.core.println.call(null,"  ",f_11952);

var G__11953 = cljs.core.next.call(null,seq__11922_11946__$1);
var G__11954 = null;
var G__11955 = (0);
var G__11956 = (0);
seq__11922_11936 = G__11953;
chunk__11923_11937 = G__11954;
count__11924_11938 = G__11955;
i__11925_11939 = G__11956;
continue;
}
} else {
}
}
break;
}
} else {
if(cljs.core.truth_(new cljs.core.Keyword(null,"arglists","arglists",1661989754).cljs$core$IFn$_invoke$arity$1(m))){
var arglists_11957 = new cljs.core.Keyword(null,"arglists","arglists",1661989754).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_((function (){var or__10752__auto__ = new cljs.core.Keyword(null,"macro","macro",-867863404).cljs$core$IFn$_invoke$arity$1(m);
if(cljs.core.truth_(or__10752__auto__)){
return or__10752__auto__;
} else {
return new cljs.core.Keyword(null,"repl-special-function","repl-special-function",1262603725).cljs$core$IFn$_invoke$arity$1(m);
}
})())){
cljs.core.prn.call(null,arglists_11957);
} else {
cljs.core.prn.call(null,((cljs.core._EQ_.call(null,new cljs.core.Symbol(null,"quote","quote",1377916282,null),cljs.core.first.call(null,arglists_11957)))?cljs.core.second.call(null,arglists_11957):arglists_11957));
}
} else {
}
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"special-form","special-form",-1326536374).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"Special Form");

cljs.core.println.call(null," ",new cljs.core.Keyword(null,"doc","doc",1913296891).cljs$core$IFn$_invoke$arity$1(m));

if(cljs.core.contains_QMARK_.call(null,m,new cljs.core.Keyword(null,"url","url",276297046))){
if(cljs.core.truth_(new cljs.core.Keyword(null,"url","url",276297046).cljs$core$IFn$_invoke$arity$1(m))){
return cljs.core.println.call(null,[cljs.core.str("\n  Please see http://clojure.org/"),cljs.core.str(new cljs.core.Keyword(null,"url","url",276297046).cljs$core$IFn$_invoke$arity$1(m))].join(''));
} else {
return null;
}
} else {
return cljs.core.println.call(null,[cljs.core.str("\n  Please see http://clojure.org/special_forms#"),cljs.core.str(new cljs.core.Keyword(null,"name","name",1843675177).cljs$core$IFn$_invoke$arity$1(m))].join(''));
}
} else {
if(cljs.core.truth_(new cljs.core.Keyword(null,"macro","macro",-867863404).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"Macro");
} else {
}

if(cljs.core.truth_(new cljs.core.Keyword(null,"repl-special-function","repl-special-function",1262603725).cljs$core$IFn$_invoke$arity$1(m))){
cljs.core.println.call(null,"REPL Special Function");
} else {
}

cljs.core.println.call(null," ",new cljs.core.Keyword(null,"doc","doc",1913296891).cljs$core$IFn$_invoke$arity$1(m));

if(cljs.core.truth_(new cljs.core.Keyword(null,"protocol","protocol",652470118).cljs$core$IFn$_invoke$arity$1(m))){
var seq__11926 = cljs.core.seq.call(null,new cljs.core.Keyword(null,"methods","methods",453930866).cljs$core$IFn$_invoke$arity$1(m));
var chunk__11927 = null;
var count__11928 = (0);
var i__11929 = (0);
while(true){
if((i__11929 < count__11928)){
var vec__11930 = cljs.core._nth.call(null,chunk__11927,i__11929);
var name = cljs.core.nth.call(null,vec__11930,(0),null);
var map__11931 = cljs.core.nth.call(null,vec__11930,(1),null);
var map__11931__$1 = ((((!((map__11931 == null)))?((((map__11931.cljs$lang$protocol_mask$partition0$ & (64))) || (map__11931.cljs$core$ISeq$))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__11931):map__11931);
var doc = cljs.core.get.call(null,map__11931__$1,new cljs.core.Keyword(null,"doc","doc",1913296891));
var arglists = cljs.core.get.call(null,map__11931__$1,new cljs.core.Keyword(null,"arglists","arglists",1661989754));
cljs.core.println.call(null);

cljs.core.println.call(null," ",name);

cljs.core.println.call(null," ",arglists);

if(cljs.core.truth_(doc)){
cljs.core.println.call(null," ",doc);
} else {
}

var G__11958 = seq__11926;
var G__11959 = chunk__11927;
var G__11960 = count__11928;
var G__11961 = (i__11929 + (1));
seq__11926 = G__11958;
chunk__11927 = G__11959;
count__11928 = G__11960;
i__11929 = G__11961;
continue;
} else {
var temp__4657__auto__ = cljs.core.seq.call(null,seq__11926);
if(temp__4657__auto__){
var seq__11926__$1 = temp__4657__auto__;
if(cljs.core.chunked_seq_QMARK_.call(null,seq__11926__$1)){
var c__11563__auto__ = cljs.core.chunk_first.call(null,seq__11926__$1);
var G__11962 = cljs.core.chunk_rest.call(null,seq__11926__$1);
var G__11963 = c__11563__auto__;
var G__11964 = cljs.core.count.call(null,c__11563__auto__);
var G__11965 = (0);
seq__11926 = G__11962;
chunk__11927 = G__11963;
count__11928 = G__11964;
i__11929 = G__11965;
continue;
} else {
var vec__11933 = cljs.core.first.call(null,seq__11926__$1);
var name = cljs.core.nth.call(null,vec__11933,(0),null);
var map__11934 = cljs.core.nth.call(null,vec__11933,(1),null);
var map__11934__$1 = ((((!((map__11934 == null)))?((((map__11934.cljs$lang$protocol_mask$partition0$ & (64))) || (map__11934.cljs$core$ISeq$))?true:false):false))?cljs.core.apply.call(null,cljs.core.hash_map,map__11934):map__11934);
var doc = cljs.core.get.call(null,map__11934__$1,new cljs.core.Keyword(null,"doc","doc",1913296891));
var arglists = cljs.core.get.call(null,map__11934__$1,new cljs.core.Keyword(null,"arglists","arglists",1661989754));
cljs.core.println.call(null);

cljs.core.println.call(null," ",name);

cljs.core.println.call(null," ",arglists);

if(cljs.core.truth_(doc)){
cljs.core.println.call(null," ",doc);
} else {
}

var G__11966 = cljs.core.next.call(null,seq__11926__$1);
var G__11967 = null;
var G__11968 = (0);
var G__11969 = (0);
seq__11926 = G__11966;
chunk__11927 = G__11967;
count__11928 = G__11968;
i__11929 = G__11969;
continue;
}
} else {
return null;
}
}
break;
}
} else {
return null;
}
}
});

//# sourceMappingURL=repl.js.map