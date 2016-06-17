# Change Log
All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/).

## [0.1.5](https://github.com/Lambda-X/lambone/compare/0.1.4...0.1.5)

- [*Breaking*] Change BOOT_BUILD_FLAVOR to BOOT_DEFAULT_FLAVOR and use it in `deps` and `test` tasks
- Fix missing `adzerk.env` dependency when backend only
- Add `hotload` dependency loading utility in the `dev` namespace
- Add `run` task
- Fix (version-file) called inside the `watch` task in frontend
- Bump `cljs-devtools` to latest
- Add some minimal boilerplate for -main argument handling
- Refer all the `boot-semver` symbols (alpha, snapshot, ...are available from the command line)

## [0.1.4](https://github.com/Lambda-X/lambone/compare/0.1.3...0.1.4)

- Add log4j2.xml for test as well
- Bump boot-reload to avoid an error
- Bump Dirac

## [0.1.3](https://github.com/Lambda-X/lambone/compare/0.1.2...0.1.3)

- Wire up version printing in the frontend
- Fix reset not found bug
- Move start/stop to system for backend

## [0.1.2](https://github.com/Lambda-X/lambone/compare/0.1.1...0.1.2)

- Remove clojure.tools.namespace calls from dev.clj
- Introduce `boot dev --dirac` for optionally have [Dirac](https://github.com/binaryage/dirac) repl in your Chrome DevTools.
- Get rid of dependency warnings

## [0.1.1](https://github.com/Lambda-X/lambone/compare/0.1.0...0.1.1)

- Now lambone serves assets in the fronted dev task
- Add src/common for shared backend/frontend code
- Add exclusions clause for org.clojure/clojure, avoids warnings, closes #1
- Add deps task to show the dependency tree, closes #2
- Add DefaultUncaughtExceptionHandler, closes #3

## [0.1.0](https://github.com/Lambda-X/lambone/compare/b7c8469...0.1.0)
### Initial Release
- First version of the template.
