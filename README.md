# Internet Of Thing

This is a small demo Electron + ClojureScript app, built for [a talk at Clojure PDX on building desktop apps with Electron and ClojureScript](http://increasinglyfunctional.com/2016/07/07/electron-clojurescript-talk/). It's based on the excellent work done on [cljs-electron](https://github.com/Gonzih/cljs-electron).

## Running it

```shell
gem install foreman              # install foreman gem (see Procfile)
npm install electron-prebuilt -g # install electron binaries

foreman start                    # compile cljs and start figwheel
electron .                       # start electron from another terminal
```

## Releasing

```shell
lein cljsbuild once frontend-release # compile ui code
lein cljsbuild once electron-release # compile electron initialization code

electron .                           # start electron to test that everything works
```

After that you can follow [distribution guide for the electron.](https://github.com/atom/electron/blob/master/docs/tutorial/application-distribution.md)
