// Clojure v1.9.0-alpha16, ClojureScript v1.9.542, js-beautify v1.6.14
// ----------------------------------------------------------------------------------------------------------
// COMPILER CONFIG:
//   arena/log_rel.cljs
//   {:elide-asserts true,
//    :main clearcut.arena.log-rel,
//    :optimizations :advanced,
//    :output-dir "test/resources/.compiled/log-rel/_workdir",
//    :output-to "test/resources/.compiled/log-rel/main.js",
//    :pseudo-names true}
// ----------------------------------------------------------------------------------------------------------

// SNIPPET #1:
//   (testing "exercise all without args"
//     (with-compiler-config {:elided-log-levels nil}
//       (fatal)
//       (error)
//       (warn)
//       (info)
//       (debug)
//       (trace)))
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

console.error();
console.error();
console.warn();
console.info();
console.log();
console.log();

// SNIPPET #2:
//   (testing "log/info"
//     (info 1 :key (js-obj)))
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

clearcut.core.log_dynamically([1, cljs.core.cst$0kw$0key, {}]);

// SNIPPET #3:
//   (testing "variadic log/info via apply"
//     (apply info ["hello," "world!"]))
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

cljs.core.apply$cljs$0core$0IFn$0_invoke$0arity$1(clearcut.core.info, new cljs.core.PersistentVector(null, 2, 5, cljs.core.PersistentVector$EMPTY_NODE, ["hello,", "world!"], null));

// SNIPPET #4:
//   (testing "variadic log/info via .call"
//     (.call info nil "hello," "world!"))
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

clearcut.core.info.call(null, "hello,", "world!");

// SNIPPET #5:
//   (testing "variadic log/info via .apply"
//     (.apply info nil #js ["hello," "world!"]))
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

clearcut.core.info.apply(null, ["hello,", "world!"]);

// SNIPPET #6:
//   (testing "trivial static logging"
//     (info "output"))
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

console.info("output");

// SNIPPET #7:
//   (testing "simple static logging"
//     (info "s" nil 42 4.2 true :key))
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

console.info("s", null, 42, 4.2, !0, cljs.core.cst$0kw$0key);

// SNIPPET #8:
//   (testing "static logging with formatting"
//     (info "s" (style "color:red") "xxx" (format "o")))
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

console.info("%s %c%s", "s", "color:red", "xxx");

// SNIPPET #9:
//   (testing "static logging with symbols"
//     (let [val 42]
//       (info val)))
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

console.info(42);

// SNIPPET #10:
//   (testing "static logging with macro-expansion"
//     (info (macro-identity "s") (macro-identity 42) (macro-identity (macro-identity :key))))
// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

console.info("s", 42, cljs.core.cst$0kw$0key);
