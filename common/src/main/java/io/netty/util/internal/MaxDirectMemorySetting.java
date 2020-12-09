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
 * This class is the preferred way to set the max direct memory instead of using
 * {@link PlatformDependent#JAVA_SYS_PROP_IO_NETTY_MAX_DIRECT_MEMORY}.
 *
 * Some infrastructure may prevent the use of Java system property from the user perspective, and configuration files
 * allow to set the max direct memory programmatically using this class before any call made
 * to {@link PlatformDependent}.
 */
public final class MaxDirectMemorySetting {

  /**
   * No check whether the max direct memory is set or not.
   */
  public static final String NO_CHECK_ENABLED = "none";

  private static final long MAX_DIRECT_MEMORY_USE_DEFAULT = -1L;

  private MaxDirectMemorySetting() {
    throw new RuntimeException("By design this class cannot be instantiated.");
  }

  private static long maxDirectMemory = MAX_DIRECT_MEMORY_USE_DEFAULT;

  public static synchronized void set(long m) {
    if (m < 0) {
      throw new IllegalArgumentException("Specified m must be greater or equal to 0");
    }

    maxDirectMemory = m;
  }

  /**
   * @return the max direct memory set or a value <= 0 if it's left to the caller to use a default value.
   */
  public static synchronized long get() {
    return maxDirectMemory;
  }
}
