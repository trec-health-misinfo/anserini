/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.anserini.search.topicreader;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;

public class HealthMisinfoTopicReader extends TopicReader<Integer> {
  public HealthMisinfoTopicReader(Path topicFile) {
    super(topicFile);
  }

  @Override
  public SortedMap<Integer, Map<String, String>> read(BufferedReader reader) throws IOException {
    SortedMap<Integer, Map<String, String>> map = new TreeMap<>();

    Document topics;
    try {
      InputStream stream =
          IOUtils.toInputStream(IOUtils.toString(reader), Charset.forName("UTF-8"));

      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      topics = dBuilder.parse(stream);

    } catch (Exception e) {
      // Eat the exception.
      return null;
    }


    NodeList nodes = topics.getElementsByTagName("topic");
    for (int i = 0; i < nodes.getLength(); i++) {
      Map<String, String> topic = new HashMap<>();

      Element cur = (Element) nodes.item(i);

      NodeList nl = cur.getElementsByTagName("query");
      if (nl.getLength() != 0) {
        topic.put("title", nl.item(0).getTextContent().trim());
      }
      int number = Integer.parseInt(cur.getElementsByTagName("number").item(0).getTextContent().trim());
      map.put(number, topic);
    }

    return map;
  }
}
