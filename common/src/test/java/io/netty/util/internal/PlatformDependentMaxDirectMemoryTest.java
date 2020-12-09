/*
 * Copyright 2015 The Netty Project
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

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public final class PlatformDependentMaxDirectMemoryTest {

  /**
   * This test tests only one case when the classloader loads the {@link PlatformDependent} class.
   * This test must be the very first method called otherwise loading the class previous to this, will
   * initialize it and dismiss this test case.
   * <p/>
   * A JUnit test runner that could use different classloader per test method would allow to test cases
   * where the MaxDirectMemorySetting check is enabled, settting vs not setting the max direct memory.
   */
  @Test
  public void testEnforceMaxDirectMemorySetException() {
    final String prop =
      System.getProperty(MaxDirectMemorySetting.JAVA_SYS_PROP_ENABLE_SIREN_CHECK,
        MaxDirectMemorySetting.NO_CHECK_ENABLED);
    System.setProperty(MaxDirectMemorySetting.JAVA_SYS_PROP_ENABLE_SIREN_CHECK, "yes");

    Throwable t = null;
    try {
      PlatformDependent.directMemoryLimit();
    } catch (Error e) {
      t = e.getCause();
    } finally {
      System.setProperty(MaxDirectMemorySetting.JAVA_SYS_PROP_ENABLE_SIREN_CHECK, prop);
    }

    assertTrue("Expected to be notified that max direct memory is not set",
      t instanceof IllegalAccessException && t.getMessage().contains("max direct memory is not set."));
  }
}
