/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */

package org.elasticsearch.xpack.ml.inference.aggs;

import org.apache.lucene.util.SetOnce;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.search.aggregations.BasePipelineAggregationTestCase;
import org.elasticsearch.search.aggregations.pipeline.BucketHelpers;
import org.elasticsearch.xpack.core.ml.inference.MlInferenceNamedXContentProvider;
import org.elasticsearch.xpack.core.ml.inference.trainedmodel.ClassificationConfigTests;
import org.elasticsearch.xpack.core.ml.inference.trainedmodel.InferenceConfig;
import org.elasticsearch.xpack.core.ml.inference.trainedmodel.RegressionConfigTests;
import org.elasticsearch.xpack.ml.inference.loadingservice.ModelLoadingService;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;

public class InferencePipelineAggregationBuilderTests extends BasePipelineAggregationTestCase<InferencePipelineAggregationBuilder> {

    private static final String NAME = "inf-agg";
/*
    @Override
    protected InferencePipelineAggregationBuilder doParseInstance(XContentParser parser) {
        return InferencePipelineAggregationBuilder.parse(new SetOnce<>(mock(ModelLoadingService.class)), NAME, parser);
    }

    @Override
    protected Writeable.Reader<InferencePipelineAggregationBuilder> instanceReader() {
        return (in) -> new InferencePipelineAggregationBuilder(in, new SetOnce<>(mock(ModelLoadingService.class)));
    }

    @Override
    protected InferencePipelineAggregationBuilder createTestInstance() {
        Map<String, String> bucketPaths = Stream.generate(() -> randomAlphaOfLength(8))
            .limit(randomIntBetween(1, 4))
            .collect(Collectors.toMap(Function.identity(), (t) -> randomAlphaOfLength(5)));

        InferencePipelineAggregationBuilder builder =
            new InferencePipelineAggregationBuilder(NAME, new SetOnce<>(mock(ModelLoadingService.class)), bucketPaths);
        builder.setModelId(randomAlphaOfLength(6));

        if (randomBoolean()) {
            builder.setGapPolicy(randomFrom(BucketHelpers.GapPolicy.values()));
        }
        if (randomBoolean()) {
            InferenceConfig config;
            if (randomBoolean()) {
                config = ClassificationConfigTests.randomClassificationConfig();
            } else {
                config = RegressionConfigTests.randomRegressionConfig();
            }
            builder.setInferenceConfig(config);
        }
        return builder;
    }

 */

    @Override
    protected InferencePipelineAggregationBuilder createTestAggregatorFactory() {
        Map<String, String> bucketPaths = Stream.generate(() -> randomAlphaOfLength(8))
            .limit(randomIntBetween(1, 4))
            .collect(Collectors.toMap(Function.identity(), (t) -> randomAlphaOfLength(5)));

        InferencePipelineAggregationBuilder builder =
            new InferencePipelineAggregationBuilder(NAME, new SetOnce<>(mock(ModelLoadingService.class)), bucketPaths);
        builder.setModelId(randomAlphaOfLength(6));

        if (randomBoolean()) {
            builder.setGapPolicy(randomFrom(BucketHelpers.GapPolicy.values()));
        }
        if (randomBoolean()) {
            InferenceConfig config;
            if (randomBoolean()) {
                config = ClassificationConfigTests.randomClassificationConfig();
            } else {
                config = RegressionConfigTests.randomRegressionConfig();
            }
            builder.setInferenceConfig(config);
        }
        return builder;
    }

    @Override
    protected NamedXContentRegistry xContentRegistry() {
        return new NamedXContentRegistry(new MlInferenceNamedXContentProvider().getNamedXContentParsers());
    }
}
