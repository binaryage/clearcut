// Clojure v1.9.0-alpha14, ClojureScript v1.9.293, js-beautify v1.6.4
// ----------------------------------------------------------------------------------------------------------
// COMPILER CONFIG:
//   arena/log_dev.cljs
//   {:elide-asserts false,
//    :main tabellion.arena.log-dev,
//    :optimizations :whitespace,
//    :output-dir "test/resources/.compiled/log-dev/_workdir",
//    :output-to "test/resources/.compiled/log-dev/main.js",
//    :pseudo-names true}
// ----------------------------------------------------------------------------------------------------------

// SNIPPET #1:
//   (testing "log/info"
//     (info 1 :key (js-obj)))
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

var _STAR_runtime_state_STAR_1 = tabellion.state._STAR_runtime_state_STAR_;
tabellion.state._STAR_runtime_state_STAR_ = tabellion.state.prepare_state.call(null, new Error, function() {
  arguments[0].apply(console, Array.prototype.slice.call(arguments, 1))
});
try {
  tabellion.core.log_dynamically.call(null, 4, [1, new cljs.core.Keyword(null, "key", "key", -3), {}])
} finally {
  tabellion.state._STAR_runtime_state_STAR_ = _STAR_runtime_state_STAR_1
};
