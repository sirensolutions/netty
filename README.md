# Siren fork of Netty

## Changes

All the parameters `io.netty.*` were prefixed with the `siren` keyword in order to avoid conflict with users possibly setting Netty's original parameters.

- io.netty.recycler.maxCapacityPerThread;
- io.netty.recycler.maxCapacity;
- io.netty.buffer.bytebuf.checkAccessible; and
- io.netty.noUnsafe.

The default value of DEFAULT_INITIAL_MAX_CAPACITY_PER_THREAD is set to 0 instead of 32k so that netty's recycler is disabled. See https://github.com/elastic/elasticsearch/pull/22452 for more context on the issues with the recycler.

## Build

With the change of defaults, some parameters need to have their original default values for the build to pass:

- set `siren.io.netty.recycler.maxCapacityPerThread` to `32768`
- set `siren.io.netty.buffer.bytebuf.checkAccessible` to `false`
- set `siren.io.netty.buffer.checkBounds` to `false`

For example, in order to build the `common` and `buffer` packages, execute the following command:

```sh
$ JAVA_HOME=/usr/lib/jvm/java-11-openjdk mvn package -pl dev-tools,common,buffer -Dsiren.io.netty.buffer.checkAccessible=true -Dsiren.io.netty.recycler.maxCapacityPerThread=32768 -Dsiren.io.netty.buffer.checkBounds=true
```

To deploy:

```sh
$ mvn clean deploy -DskipTests=true -pl dev-tools,common,buffer -P artifactory -Dartifactory_username=<USERNAME> -Dartifactory_password=<PASSWORD>
```

# Netty Project

Netty is an asynchronous event-driven network application framework for rapid development of maintainable high performance protocol servers & clients.

## Links

* [Web Site](https://netty.io/)
* [Downloads](https://netty.io/downloads.html)
* [Documentation](https://netty.io/wiki/)
* [@netty_project](https://twitter.com/netty_project)

## How to build

For the detailed information about building and developing Netty, please visit [the developer guide](https://netty.io/wiki/developer-guide.html).  This page only gives very basic information.

You require the following to build Netty:

* Latest stable [Oracle JDK 7](http://www.oracle.com/technetwork/java/)
* Latest stable [Apache Maven](http://maven.apache.org/)
* If you are on Linux, you need [additional development packages](https://netty.io/wiki/native-transports.html) installed on your system, because you'll build the native transport.

Note that this is build-time requirement.  JDK 5 (for 3.x) or 6 (for 4.0+) is enough to run your Netty-based application.

## Branches to look

Development of all versions takes place in each branch whose name is identical to `<majorVersion>.<minorVersion>`.  For example, the development of 3.9 and 4.0 resides in [the branch '3.9'](https://github.com/netty/netty/tree/3.9) and [the branch '4.0'](https://github.com/netty/netty/tree/4.0) respectively.

## Usage with JDK 9

Netty can be used in modular JDK9 applications as a collection of automatic modules. The module names follow the
reverse-DNS style, and are derived from subproject names rather than root packages due to historical reasons. They
are listed below:

 * `io.netty.all`
 * `io.netty.buffer`
 * `io.netty.codec`
 * `io.netty.codec.dns`
 * `io.netty.codec.haproxy`
 * `io.netty.codec.http`
 * `io.netty.codec.http2`
 * `io.netty.codec.memcache`
 * `io.netty.codec.mqtt`
 * `io.netty.codec.redis`
 * `io.netty.codec.smtp`
 * `io.netty.codec.socks`
 * `io.netty.codec.stomp`
 * `io.netty.codec.xml`
 * `io.netty.common`
 * `io.netty.handler`
 * `io.netty.handler.proxy`
 * `io.netty.resolver`
 * `io.netty.resolver.dns`
 * `io.netty.transport`
 * `io.netty.transport.epoll` (`native` omitted - reserved keyword in Java)
 * `io.netty.transport.kqueue` (`native` omitted - reserved keyword in Java)
 * `io.netty.transport.unix.common` (`native` omitted - reserved keyword in Java)
 * `io.netty.transport.rxtx`
 * `io.netty.transport.sctp`
 * `io.netty.transport.udt`



Automatic modules do not provide any means to declare dependencies, so you need to list each used module separately
in your `module-info` file.
