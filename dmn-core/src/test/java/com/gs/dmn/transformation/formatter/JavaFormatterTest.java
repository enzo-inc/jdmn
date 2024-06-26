/*
 * Copyright 2016 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.gs.dmn.transformation.formatter;

import com.gs.dmn.AbstractTest;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class JavaFormatterTest extends AbstractTest {
    protected void doTest() throws IOException {
        doTest("TFnLibrary.txt");
        doTest("TFnLibraryImpl.txt");
        doTest("FnLibrary.txt");
    }

    protected void doTest(String fileName) throws IOException {
        String code = readResource("formatter/" + fileName);
        JavaFormatter formatter = getFormatter();
        String actualContent = formatter.formatSource(code).replace("    \r", "\r").replace("\r", "");
        String expectedContent = readResource(getExpectedRelativePath() + fileName).replace("    \r", "\r").replace("\r", "");
        assertEquals(expectedContent, actualContent, "Error for " + fileName);
    }

    private String readResource(String actualPath) throws IOException {
        File file = new File(resource(actualPath));
        return FileUtils.readFileToString(file, "UTF-8");
    }

    protected abstract JavaFormatter getFormatter();

    protected abstract String getExpectedRelativePath();
}
