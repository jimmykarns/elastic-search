/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.core.ml.inference.results;

import org.elasticsearch.common.io.stream.Writeable;
import org.elasticsearch.common.xcontent.ConstructingObjectParser;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.ingest.IngestDocument;
import org.elasticsearch.test.AbstractSerializingTestCase;
import org.elasticsearch.test.AbstractWireSerializingTestCase;
import org.elasticsearch.xpack.core.ml.inference.trainedmodel.InferenceConfig;
import org.elasticsearch.xpack.core.ml.inference.trainedmodel.RegressionConfig;
import org.elasticsearch.xpack.core.ml.inference.trainedmodel.RegressionConfigTests;
import org.elasticsearch.xpack.core.ml.utils.MapHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;


public class RegressionInferenceResultsTests extends AbstractWireSerializingTestCase<RegressionInferenceResults> {

//    @SuppressWarnings("unchecked")
//    private static final ConstructingObjectParser<RegressionInferenceResultsBuilder, InferenceConfig> PARSER =
//        new ConstructingObjectParser<>("regression_result", false,
//            (args, context) -> new RegressionInferenceResultsBuilder((Double) args[0], (String) args[1]);
//        );
//
//    static {
//        PARSER.declareString(constructorArg(), new ParseField(SingleValueInferenceResults.FEATURE_NAME));
//        PARSER.declareDouble(constructorArg(), new ParseField(SingleValueInferenceResults.IMPORTANCE));
//        PARSER.declareObject(optionalConstructorArg(), (p, c) -> p.map(HashMap::new, XContentParser::doubleValue),
//            new ParseField(FeatureImportance.CLASS_IMPORTANCE));
//    }

    public static RegressionInferenceResults createRandomResults() {
        return new RegressionInferenceResults(randomDouble(),
            RegressionConfigTests.randomRegressionConfig(),
            randomBoolean() ? null :
                Stream.generate(FeatureImportanceTests::randomRegression)
                    .limit(randomIntBetween(1, 10))
                    .collect(Collectors.toList()));
    }

    public void testWriteResults() {
        RegressionInferenceResults result = new RegressionInferenceResults(0.3, RegressionConfig.EMPTY_PARAMS);
        IngestDocument document = new IngestDocument(new HashMap<>(), new HashMap<>());
        result.writeResult(document, "result_field");

        assertThat(document.getFieldValue("result_field.predicted_value", Double.class), equalTo(0.3));
    }

    public void testWriteResultsToMap() {
        RegressionInferenceResults result = new RegressionInferenceResults(0.3, RegressionConfig.EMPTY_PARAMS);
        Map<String, Object> doc = result.writeResultToMap("result_field");

        Object value = MapHelper.dig("result_field.predicted_value", doc);
        assertThat(value, equalTo(0.3));
    }

    public void testWriteResultsWithImportance() {
        List<FeatureImportance> importanceList = Stream.generate(FeatureImportanceTests::randomRegression)
            .limit(5)
            .collect(Collectors.toList());
        RegressionInferenceResults result = new RegressionInferenceResults(0.3,
            new RegressionConfig("predicted_value", 3),
            importanceList);
        IngestDocument document = new IngestDocument(new HashMap<>(), new HashMap<>());
        result.writeResult(document, "result_field");

        assertThat(document.getFieldValue("result_field.predicted_value", Double.class), equalTo(0.3));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> writtenImportance = (List<Map<String, Object>>)document.getFieldValue(
            "result_field.feature_importance",
            List.class);
        assertThat(writtenImportance, hasSize(3));
        importanceList.sort((l, r)-> Double.compare(Math.abs(r.getImportance()), Math.abs(l.getImportance())));
        for (int i = 0; i < 3; i++) {
            Map<String, Object> objectMap = writtenImportance.get(i);
            FeatureImportance importance = importanceList.get(i);
            assertThat(objectMap.get("feature_name"), equalTo(importance.getFeatureName()));
            assertThat(objectMap.get("importance"), equalTo(importance.getImportance()));
            assertThat(objectMap.size(), equalTo(2));
        }
    }

    @Override
    protected RegressionInferenceResults createTestInstance() {
        return createRandomResults();
    }

    @Override
    protected Writeable.Reader<RegressionInferenceResults> instanceReader() {
        return RegressionInferenceResults::new;
    }

//    @Override
//    protected RegressionInferenceResults doParseInstance(XContentParser parser) throws IOException {
//        return PARSER.apply(parser, null).build();
//    }

    private static class RegressionInferenceResultsBuilder {

        private final String resultsField;
        private final double value;
        private List<FeatureImportance> featureImportance;
        private Integer numTopFeatures;

        private RegressionInferenceResultsBuilder(double value, String resultsField) {
            this.resultsField = resultsField;
            this.value = value;
        }

        private void setNumTopFeatures(Integer numTopFeatures) {
            this.numTopFeatures = numTopFeatures;
        }

        private void setFeatureImportance(List<FeatureImportance> featureImportance) {
            this.featureImportance = featureImportance;
        }

        private RegressionInferenceResults build() {
            if (featureImportance != null) {
                return new RegressionInferenceResults(value, resultsField,
                    numTopFeatures == null ? featureImportance.size() : numTopFeatures,
                    featureImportance);
            } else {
                return new RegressionInferenceResults(value, resultsField);
            }
        }
    }
}
