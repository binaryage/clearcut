// Clojure v1.9.0-alpha16, ClojureScript v1.9.542, js-beautify v1.6.14
// ----------------------------------------------------------------------------------------------------------
// COMPILER CONFIG:
//   arena/log_dev.cljs
//   {:elide-asserts false,
//    :main clearcut.arena.log-dev,
//    :optimizations :whitespace,
//    :output-dir "test/resources/.compiled/log-dev/_workdir",
//    :output-to "test/resources/.compiled/log-dev/main.js",
//    :pseudo-names true}
// ----------------------------------------------------------------------------------------------------------

// SNIPPET #1:
//   (testing "log/info"
//     (info 1 :key (js-obj)))
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

var _STAR_runtime_state_STAR_1 = clearcut.state._STAR_runtime_state_STAR_;
clearcut.state._STAR_runtime_state_STAR_ = clearcut.state.prepare_state.call(null, new Error, function() {
  arguments[0].apply(console, Array.prototype.slice.call(arguments, 1))
});
try {
  clearcut.core.log_dynamically.call(null, 4, [1, new cljs.core.Keyword(null, "key", "key", -3), {}])
} finally {
  clearcut.state._STAR_runtime_state_STAR_ = _STAR_runtime_state_STAR_1
}

// SNIPPET #2:
//   (testing "variadic log/info via apply"
//     (apply info ["hello," "world!"]))
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

cljs.core.apply.call(null, clearcut.core.info, new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector.EMPTY_NODE, ["hello,", "world!"], null));

// SNIPPET #3:
//   (testing "variadic log/info via .call"
//     (.call info nil "hello," "world!"))
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

clearcut.core.info.call(null, "hello,", "world!");

// SNIPPET #4:
//   (testing "variadic log/info via .apply"
//     (.apply info nil #js ["hello," "world!"]))
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

clearcut.core.info.apply(null, ["hello,", "world!"]);
