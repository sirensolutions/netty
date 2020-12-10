/*
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util.internal;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is the preferred way to set the max direct memory instead of using
 * {@link PlatformDependent#JAVA_SYS_PROP_IO_NETTY_MAX_DIRECT_MEMORY}.
 *
 * Some infrastructure may prevent the use of Java system property from the user perspective, and configuration files
 * allow to set the max direct memory programmatically using this class before any call made
 * to {@link PlatformDependent}.
 */
public final class MaxDirectMemorySetting {

  private static final InternalLogger logger = InternalLoggerFactory.getInstance(MaxDirectMemorySetting.class);

  private static final long MAX_DIRECT_MEMORY_USE_DEFAULT = -1L;

  private MaxDirectMemorySetting() {
    throw new RuntimeException("By design this class cannot be instantiated.");
  }

  private static long maxDirectMemory = MAX_DIRECT_MEMORY_USE_DEFAULT;

  public static synchronized void set(long m) {
    if (m < 0) {
      throw new IllegalArgumentException("Specified max_direct_memory must be greater or equal to 0, got " + m);
    }

    maxDirectMemory = m;
  }

  /**
   * @return the max direct memory set or a value <= 0 if it's left to the caller to use a default value.
   */
  public static synchronized long get() {
    return maxDirectMemory;
  }

  /**
   * Copied from {@link PlatformDependent#MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN()}.
   */
  private static final Pattern MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN = Pattern.compile(
    "\\s*-XX:MaxDirectMemorySize\\s*=\\s*([0-9]+)\\s*([kKmMgG]?)\\s*$");

  /**
   * Copied from {@link PlatformDependent#maxDirectMemory0()} to prevent the classloader
   * to load {@link PlatformDependent} before this class is loaded.
   */
  public static long maxDirectMemory0() {
    long maxDirectMemory = 0;

    ClassLoader systemClassLoader = null;
    try {
      systemClassLoader = PlatformDependent0.getSystemClassLoader();

      // When using IBM J9 / Eclipse OpenJ9 we should not use VM.maxDirectMemory() as it not reflects the
      // correct value.
      // See:
      //  - https://github.com/netty/netty/issues/7654
      String vmName = SystemPropertyUtil.get("java.vm.name", "").toLowerCase();
      if (!vmName.startsWith("ibm j9") &&
        // https://github.com/eclipse/openj9/blob/openj9-0.8.0/runtime/include/vendor_version.h#L53
        !vmName.startsWith("eclipse openj9")) {
        // Try to get from sun.misc.VM.maxDirectMemory() which should be most accurate.
        Class<?> vmClass = Class.forName("sun.misc.VM", true, systemClassLoader);
        Method m = vmClass.getDeclaredMethod("maxDirectMemory");
        maxDirectMemory = ((Number) m.invoke(null)).longValue();
      }
    } catch (Throwable ignored) {
      // Ignore
    }

    if (maxDirectMemory > 0) {
      return maxDirectMemory;
    }

    try {
      // Now try to get the JVM option (-XX:MaxDirectMemorySize) and parse it.
      // Note that we are using reflection because Android doesn't have these classes.
      Class<?> mgmtFactoryClass = Class.forName(
        "java.lang.management.ManagementFactory", true, systemClassLoader);
      Class<?> runtimeClass = Class.forName(
        "java.lang.management.RuntimeMXBean", true, systemClassLoader);

      Object runtime = mgmtFactoryClass.getDeclaredMethod("getRuntimeMXBean").invoke(null);

      @SuppressWarnings("unchecked")
      List<String> vmArgs = (List<String>) runtimeClass.getDeclaredMethod("getInputArguments").invoke(runtime);
      for (int i = vmArgs.size() - 1; i >= 0; i--) {
        Matcher m = MAX_DIRECT_MEMORY_SIZE_ARG_PATTERN.matcher(vmArgs.get(i));
        if (!m.matches()) {
          continue;
        }

        maxDirectMemory = Long.parseLong(m.group(1));
        switch (m.group(2).charAt(0)) {
          case 'k':
          case 'K':
            maxDirectMemory *= 1024;
            break;
          case 'm':
          case 'M':
            maxDirectMemory *= 1024 * 1024;
            break;
          case 'g':
          case 'G':
            maxDirectMemory *= 1024 * 1024 * 1024;
            break;
        }
        break;
      }
    } catch (Throwable ignored) {
      // Ignore
    }

    if (maxDirectMemory <= 0) {
      maxDirectMemory = Runtime.getRuntime().maxMemory();
      logger.debug("maxDirectMemory: {} bytes (maybe)", maxDirectMemory);
    } else {
      logger.debug("maxDirectMemory: {} bytes", maxDirectMemory);
    }

    return maxDirectMemory;
  }
}
