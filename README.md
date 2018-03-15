# Siren fork of Netty

## Changes

The following parameters have been prefixed with the `siren` keyword in order to avoid conflict with users possibly setting Netty's original parameters:

- io.netty.recycler.maxCapacityPerThread;
- io.netty.recycler.maxCapacity;
- io.netty.buffer.bytebuf.checkAccessible; and
- io.netty.noUnsafe.

The default value of DEFAULT_INITIAL_MAX_CAPACITY_PER_THREAD is set to 0 instead of 32k so that netty's recycler is disabled. See https://github.com/elastic/elasticsearch/pull/22452 for more context on the issues with the recycler.

## Build

With the change of defaults, some parameters need to have their original default values for the build to pass:

- set `siren.io.netty.recycler.maxCapacityPerThread` to `32768`
- set `siren.io.netty.buffer.bytebuf.checkAccessible` to `false`

For example, in order to build the `common` and `buffer` packages, execute the following command:

```sh
$ JAVA_HOME=/usr/lib/jvm/java-8-jdk mvn package -pl common,buffer -Dsiren.io.netty.buffer.bytebuf.checkAccessible=true -Dsiren.io.netty.recycler.maxCapacityPerThread=32768
```

To deploy:

```sh
$ mvn clean deploy -Dmaven.test.skip=true -pl common,buffer -P artifactory -Dartifactory_username=<USERNAME> -Dartifactory_password=<PASSWORD>
```

# Netty Project

Netty is an asynchronous event-driven network application framework for rapid development of maintainable high performance protocol servers & clients.

## Links

* [Web Site](http://netty.io/)
* [Downloads](http://netty.io/downloads.html)
* [Documentation](http://netty.io/wiki/)
* [@netty_project](https://twitter.com/netty_project)

## How to build

For the detailed information about building and developing Netty, please visit [the developer guide](http://netty.io/wiki/developer-guide.html).  This page only gives very basic information.

You require the following to build Netty:

* Latest stable [Oracle JDK 7](http://www.oracle.com/technetwork/java/)
* Latest stable [Apache Maven](http://maven.apache.org/)
* If you are on Linux, you need [additional development packages](http://netty.io/wiki/native-transports.html) installed on your system, because you'll build the native transport.

Note that this is build-time requirement.  JDK 5 (for 3.x) or 6 (for 4.0+) is enough to run your Netty-based application.

## Branches to look

Development of all versions takes place in each branch whose name is identical to `<majorVersion>.<minorVersion>`.  For example, the development of 3.9 and 4.0 resides in [the branch '3.9'](https://github.com/netty/netty/tree/3.9) and [the branch '4.0'](https://github.com/netty/netty/tree/4.0) respectively.
