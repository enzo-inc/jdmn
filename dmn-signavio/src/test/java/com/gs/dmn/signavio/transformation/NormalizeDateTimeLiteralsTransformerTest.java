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
package com.gs.dmn.signavio.transformation;

import com.gs.dmn.DMNModelRepository;
import com.gs.dmn.ast.TDefinitions;
import com.gs.dmn.runtime.Pair;
import com.gs.dmn.serialization.DMNNamespacePrefixMapper;
import com.gs.dmn.serialization.PrefixNamespaceMappings;
import com.gs.dmn.signavio.SignavioDMNModelRepository;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class NormalizeDateTimeLiteralsTransformerTest extends AbstractSignavioFileTransformerTest {
    private final NormalizeDateTimeLiteralsTransformer transformer = new NormalizeDateTimeLiteralsTransformer(LOGGER);

    @Test
    public void testNormalize() {
        assertNull(transformer.normalize(null));
        assertEquals("", transformer.normalize(""));

        assertEquals("date and time(\"1900-01-01T00:00:00Z\")", transformer.normalize("date and time(\"1899-12-31T19:00:00-0500\")"));

        assertEquals("time(\"00:00:00Z\")", transformer.normalize("time(\"T19:00:00-0500\")"));
        assertEquals("time(\"00:00:00Z\")", transformer.normalize("time(\"19:00:00-0500\")"));
    }

    @Test
    public void testTransform() throws Exception {
        String path = "dmn/input/1.1/";

        // Transform DMN
        File dmnFile = new File(resource(path + "Null Safe Tests.dmn"));
        Pair<TDefinitions, PrefixNamespaceMappings> pair = dmnReader.read(dmnFile);
        DMNModelRepository repository = new SignavioDMNModelRepository(pair);
        DMNModelRepository actualRepository = transformer.transform(repository);

        // Check output
        checkDefinitions(actualRepository, "Normalized Null Safe Tests.dmn");
    }

    private void checkDefinitions(DMNModelRepository repository, String fileName) throws Exception {
        File actualDMNFile = new File("target/" + fileName);
        TDefinitions actualDefinitions = repository.getRootDefinitions();
        dmnWriter.write(actualDefinitions, actualDMNFile, new DMNNamespacePrefixMapper(actualDefinitions.getNamespace(), "sig"));

        String path = "dmn/expected/";
        File expectedDMNFile = new File(resource(path + fileName));

        compareFile(expectedDMNFile, actualDMNFile);
    }
}