# clearcut

[![GitHub license](https://img.shields.io/github/license/binaryage/clearcut.svg)](license.txt) 
[![Clojars Project](https://img.shields.io/clojars/v/binaryage/clearcut.svg)](https://clojars.org/binaryage/clearcut) 
[![Travis](https://img.shields.io/travis/binaryage/clearcut.svg)](https://travis-ci.org/binaryage/clearcut) 
 
This is a library offering a thin unified logging layer on top of console.log (ClojureScript) and clojure.tools.logging (Clojure).

Clearcut helps you write logging code which can be copy&pasted between Clojure and ClojureScript or used in cljc files directly.

Design goals:

  1. use log levels matching `clojure.tools.logging`
  1. `:log-level` elides unwanted logs at compile-time
  1. ClojureScript (dev mode):
     1. be `cljs-devtools` friendly (do not stringify parameters)
     1. support styling
     1. display namespaces
  1. ClojureScript (release mode):
     1. stringify parameters (pprint)
     1. limit output size
     1. display namespaces
  1. Clojure
     1. delegate to `clojure.tools.logging`
     1. convert styling to ANSI colors or optionally strip it
