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

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class MaxDirectMemorySettingTest {

  /**
   * This test tests only one case when the classloader loads the {@link PlatformDependent} class.
   * This test must be the very first method called otherwise loading the class previous to this, will
   * initialize it and dismiss this test case.
   * <p/>
   * A JUnit test runner that could use different classloader per test method would allow to test cases
   * where the MaxDirectMemorySetting check is enabled, setting vs not setting the max direct memory.
   */
  @Test
  @Ignore("see test comment")
  public void testSetMaxDirectMemory() {
    MaxDirectMemorySetting.set(2012);
    assertEquals(MaxDirectMemorySetting.get(), PlatformDependent.directMemoryLimit());
  }
}
