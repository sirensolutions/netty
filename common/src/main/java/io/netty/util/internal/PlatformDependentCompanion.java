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

/**
 * This method is the preferred way to set the max direct memory instead of using
 * {@link PlatformDependent#JAVA_SYS_PROP_IO_NETTY_MAX_DIRECT_MEMORY}.
 *
 * Optionally, one can check that the max direct memory is set prior to calling {@link #getMaxDirectMemory()} by using
 * {@link #checkSetMaxDirectMemoryWasCalled()}. To prevent failing the check, by default it is disabled if when called.
 * To enable it one must set the Java system property {@link #JAVA_SYS_PROP_ENABLE_SIREN_CHECK} to 'yes'.
 */
public final class PlatformDependentCompanion {

  /**
   * No check whether the max direct memory is set or not.
   */
  public static final String NO_CHECK = "none";

  /**
   * When this Java system property is set to anything but the string {@link #NO_CHECK}, the
   * {@link #checkSetMaxDirectMemoryWasCalled()} with throw
   * an {@link IllegalAccessException} if the max direct memory is not set.
   */
  public static final String JAVA_SYS_PROP_ENABLE_SIREN_CHECK = "siren.memory.direct.max.check.enable";

  private static final InternalLogger logger = InternalLoggerFactory.getInstance(PlatformDependentCompanion.class);

  private static final long MAX_DIRECT_MEMORY_NOT_SET = -2L;
  private static final long MAX_DIRECT_MEMORY_USE_DEFAULT = -1L;

  private PlatformDependentCompanion() {
    throw new RuntimeException("By design this class cannot be instantiated.");
  }

  private static long maxDirectMemory = MAX_DIRECT_MEMORY_NOT_SET;

  public static synchronized void setUseDefaultMaxDirectMemory() {
    maxDirectMemory = MAX_DIRECT_MEMORY_USE_DEFAULT;
  }

  public static synchronized void setMaxDirectMemory(long m) {
    if (m < 0) {
      throw new IllegalArgumentException("Specified m must be greater or equal to 0");
    }

    maxDirectMemory = m;
  }

  public static synchronized long getMaxDirectMemory() {
    return maxDirectMemory;
  }

  /**
   * One can call this method to check that {@link #setMaxDirectMemory(long)} was called.
   *
   * @throws IllegalAccessException if {@link #setMaxDirectMemory(long)} was not called and Java system
   *                                property {@link #JAVA_SYS_PROP_ENABLE_SIREN_CHECK} is set;
   *                                otherwise it just returns.
   */
  public static synchronized void checkSetMaxDirectMemoryWasCalled() throws IllegalAccessException {
    if (System.getProperty(JAVA_SYS_PROP_ENABLE_SIREN_CHECK, NO_CHECK).equals(NO_CHECK)) {
      logger.warn("checkSetMaxDirectMemoryWasCalled() and is disabled. To enable this check, " +
        "set Java system property '{}' to yes", JAVA_SYS_PROP_ENABLE_SIREN_CHECK);
      return;
    }

    if (maxDirectMemory == MAX_DIRECT_MEMORY_NOT_SET) {
      throw new IllegalAccessException("max direct memory is not set.");
    }
  }
}
