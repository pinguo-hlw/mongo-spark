/*
 * Copyright (c) 2008-2015 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.mongodb.spark;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;

import org.bson.Document;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MongoSparkContextTest {
    private String master = "local";
    private String appName = "testApp";
    private String sparkHome = "path/to/spark";
    private String jarFile = "test.jar";

    private SparkConf sparkConf = new SparkConf().setMaster(master).setAppName(appName);
    private MongoSparkContext msc;

    private String username = "test";
    private String password = "password";
    private String host = "localhost:27017";
    private String database = "test";
    private String collection = "test";
    private MongoClientURI uri =
            new MongoClientURI("mongodb://" + username + ":" + password + "@" + host + "/" + database + "." + collection);

    private MongoClient client = new MongoClient(uri);

    private List<Document> documents = Arrays.asList(new Document("a", 1), new Document("a", 2), new Document("a", 3));

    private long lower = 1;
    private long upper = 3;
    private String key = "a";

    @Before
    public void setUp() {
        client.getDatabase(uri.getDatabase()).getCollection(uri.getCollection()).drop();
        client.getDatabase(uri.getDatabase()).getCollection(uri.getCollection()).insertMany(documents);
    }

    @After
    public void tearDown() {
        msc.stop();
        msc = null;
    }

    @Test
    public void shouldConstructMSCWithSparkConf() {
        msc = new MongoSparkContext(sparkConf, uri);

        MongoRDD rdd = msc.parallelize(lower, upper, key);

        Assert.assertEquals(documents.size(), rdd.collect().length);
        Assert.assertEquals(master, msc.sc().master());
        Assert.assertEquals(appName, msc.sc().appName());
    }

    @Test
    public void shouldConstructMSCWithSparkContext() {
        msc = new MongoSparkContext(new SparkContext(sparkConf), uri);

        MongoRDD rdd = msc.parallelize(lower, upper, key);

        Assert.assertEquals(documents.size(), rdd.collect().length);
        Assert.assertEquals(master, msc.sc().master());
        Assert.assertEquals(appName, msc.sc().appName());
    }

    @Test
    public void shouldConstructMSCWithMasterAppNameCredentialsHostsOptions() {
        msc = new MongoSparkContext(master, appName, uri);

        MongoRDD rdd = msc.parallelize(lower, upper, key);

        Assert.assertEquals(documents.size(), rdd.collect().length);
        Assert.assertEquals(master, msc.sc().master());
        Assert.assertEquals(appName, msc.sc().appName());
    }

    @Test
    public void shouldConstructMSCWithMasterAppNameConfCredentialsHostsOptions() {
        msc = new MongoSparkContext(master, appName, sparkConf, uri);

        MongoRDD rdd = msc.parallelize(lower, upper, key);

        Assert.assertEquals(documents.size(), rdd.collect().length);
        Assert.assertEquals(master, msc.sc().master());
        Assert.assertEquals(appName, msc.sc().appName());
    }

    @Test
    public void shouldConstructMSCWithMasterAppNameSparkHomeJarFileCredentialsHostsOptions() {
        msc = new MongoSparkContext(master, appName, sparkHome, jarFile, uri);

        MongoRDD rdd = msc.parallelize(lower, upper, key);

        Assert.assertEquals(documents.size(), rdd.collect().length);
        Assert.assertEquals(master, msc.sc().master());
        Assert.assertEquals(appName, msc.sc().appName());
    }

    @Test
    public void shouldConstructMSCWithMasterAppNameSparkHomeJarsCredentialsHostsOptions() {
        msc = new MongoSparkContext(master, appName, sparkHome, new String[] {jarFile}, uri);

        MongoRDD rdd = msc.parallelize(lower, upper, key);

        Assert.assertEquals(documents.size(), rdd.collect().length);
        Assert.assertEquals(master, msc.sc().master());
        Assert.assertEquals(appName, msc.sc().appName());
    }
}