/*
 * ******************************************************************************
 *   Copyright  2020 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.utilities;

import com.ellucian.ethos.integration.client.config.SemVer;
import com.ellucian.ethos.integration.client.proxy.filter.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class UtiltityTest {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    private final static Log log = LogFactory.getLog(UtiltityTest.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    // ==========================================================================
    // Methods
    // ==========================================================================

    @Test
    void testReadingJson() throws Exception {
        String jsonStr = "[ {" +
                                "\"jsonPath\": \"/\"," +
                                "\"jsonProperty\": \"id\"" +
                           "}," +
                           "{" +
                                "\"jsonPath\": \"/\"," +
                                "\"jsonProperty\": \"title\"," +
                                "\"breakfast\": {" +
                                    "\"hotBreakfast\": \"oatmeal\"," +
                                    "\"coldBreakfast\": \"cheerios\"" +
                                "}" +
                           "}," +
                           "{" +
                                "\"jsonPath\": \"/\"," +
                                "\"jsonProperty\": \"description\"" +
                           "}," +
                           "{" +
                                "\"jsonPath\": \"/\"," +
                                "\"jsonProperty\": \"activeIndicator\"" +
                           "}," +
                           "{" +
                                "\"jsonPath\": \"/\"," +
                                "\"jsonProperty\": \"webIndicator\"" +
                           "} ]";
        JsonNode jsonNode = objectMapper.readTree( jsonStr );
//        ObjectMapper objectMapper = new ObjectMapper();
//        JsonNode jsonNode = objectMapper.readTree( jsonStr );
        System.out.println( ">>> JSON NODE SIZE: " + jsonNode.size() );
//        System.out.println( ">>> AS COLLECTION SIZE: " + jsonNode.asCollection().size() );
//        System.out.println( ">>> AS LIST SIZE: " + jsonNode.asList().size() );
        Iterator<JsonNode> iter = jsonNode.iterator();
//        System.out.println(">>> ITERATOR SIZE: " + iter.size() );
        while(iter.hasNext()) {
            JsonNode instanceNode = iter.next();
            System.out.println(">>> JSON PATH: " + instanceNode.get("jsonPath").asText() );
            System.out.println(">>> JSON PROPERTY1: " + instanceNode.get("jsonProperty").asText() );
//            if( instanceNode.at("/breakfast/hotBreakfast") != null ) {
                System.out.println(">>> JSON PROPERTY2 BLANK? " + (instanceNode.at("/breakfast/hotBreakfast").asText().isBlank() ? "YES" : "NO") +  ", JSON PROPERTY2: " + instanceNode.at("/breakfast/hotBreakfast").asText());
//            }
        }
    }

    @Test
    public void jsonNodeArrayTest() throws Exception {
        String jsonStr = "{" +
                              "\"jsonPath\": \"/\"," +
                              "\"jsonProperty\": \"title\"," +
                              "\"breakfast\": {" +
                                  "\"hotBreakfast\": \"oatmeal\"," +
                                  "\"coldBreakfast\": \"cheerios\"" +
                              "}" +
                         "}";
        JsonNode node = objectMapper.readTree( jsonStr );
//        ObjectNode objectNode = JsonNodeFactory.withExactBigDecimals(false).objectNode();
//        objectNode.put("id", "appId");
//        ArrayNode arrayNode = objectNode.putArray( "resource" );
        ArrayNode arrayNode = JsonNodeFactory.withExactBigDecimals(false).arrayNode();
        for( int i =0; i < 4; i++ ) {
            arrayNode.add( node );
        }
//        objectNode.set( "resource", arrayNode );
        System.out.println( arrayNode.toPrettyString() );
    }

    @Test
    public void filterSemanticTest() {
        List<String> stringList = new ArrayList<>();
        stringList.add( "v9" );
        stringList.add( "v10.0.1" );
        stringList.add( "v10" );
        stringList.add( "v11.2.3" );
        stringList.add( "v12.3" );
        stringList.add( "v12.2.4" );
        stringList.add( "v12" );
        List<SemVer> resultList = new ArrayList<>();
        for( String verStr : stringList ) {
            if( verStr.contains(".") ) {
                verStr = verStr.substring( 0, verStr.indexOf('.') );
            }
            verStr += ".0.0";
            SemVer semVer = new SemVer.Builder(verStr.substring(1)).build();
            if( resultList.contains(semVer) ) {
                continue;
            }
            resultList.add( semVer );
        }
        System.out.println( "RESULT LIST: " + resultList.toString() );
        Collections.sort(resultList);
        System.out.println( "SORTED ASCENDING RESULT LIST: " + resultList.toString() );
        Collections.reverse(resultList);
        System.out.println( "SORTED DESCENDING RESULT LIST: " + resultList.toString() );
    }

    @Test
    public void jsonPathArrayTest() throws Exception {
        String ownerOverridesStr = "{\"ownerOverrides\": [" +
                "        {" +
                "            \"resourceName\": \"account-funds-available\"," +
                "            \"applicationId\": \"1d6bd816-7018-49ff-8eea-af696688472e\"" +
                "        }" +
                "    ]}";
        JsonNode jsonNode = objectMapper.readTree( ownerOverridesStr );
        JsonNode ownerOverridesNode = jsonNode.at("/ownerOverrides");
        Iterator<JsonNode> jsonNodeIterator = ownerOverridesNode.iterator();
        while( jsonNodeIterator.hasNext() ) {
            JsonNode node = jsonNodeIterator.next();
            String txt = node.at("/resourceName").asText();
            System.out.println( "TXT: " + txt );
        }
    }

    @Test
    public void intTest() {
        double num1 = 6000000 * 1073;
        System.out.println( "NUM1: " + NumberFormat.getInstance().format(num1));
//        Integer num2 = null;
//        System.out.println( "NUM2: " + (num2 * 1000) );
        double doubleVal = 8.0;
        int intVal = 8;
        System.out.println( "VALS EQUAL? " + (doubleVal == intVal) );
        System.out.println( "VAL AS INT: " + (int)num1 );
    }

    @Test
    public void mapTest() {
        Map<String,String> myMap = new HashMap<>();
        myMap.put( "someKey", null );
        myMap.put( "someOtherKey", "" );
        System.out.println( myMap.toString() );
    }

    @Test
    public void stringEncodingDecodingTest() throws IOException {
        CriteriaFilter criteriaFilter = new CriteriaFilter.Builder()
//                .withCriteriaSet("names", "firstName", "John")
                .withCriteriaSet("firstName", "John")
                .build();
        String encodedStr = URLEncoder.encode( criteriaFilter.toString(), "UTF-8" );
        String decodedStr = URLDecoder.decode( encodedStr, "UTF-8" );
        System.out.println( "CRITERIA STR: " + criteriaFilter.toString() );
        System.out.println( "ENCODED STR: " + encodedStr );
        System.out.println( "DECODED STR: " + decodedStr );
        assert( decodedStr.equals(criteriaFilter.toString()) );
    }

    @Test
    public void percentTest() {
        int total = 10;
        double offset = total * 0.95;
        System.out.println( "OFFSET: " + (int)offset );
    }

    @Test
    public void guidTest() {
        String str = "\"" + UUID.randomUUID().toString() + "\"";
        System.out.println( str );
    }

    @Test
    public void equalityTest() {
        String version1 = "v1";
        String version2 = "V2";
        System.out.println( "version1 startsWith lowercase v: " + version1.startsWith("v"));
        System.out.println( "version1 startsWith uppercase V: " + version1.startsWith("V"));
        System.out.println( "version2 startsWith lowercase v: " + version2.startsWith("v"));
        System.out.println( "version2 startsWith uppercase V: " + version2.startsWith("V"));
    }

    @Test
    public void criteriaTest() {
//        String spacingPattern = "%-50s%-40s%-45s%n";
//        System.out.printf( spacingPattern, "SDK EXAMPLE", "SAMPLE FILTER NAME", "EXAMPLE FILTER" );
//        CriteriaFilter cf = new CriteriaFilter.Builder()
//                            .withSimpleCriteria( "lastName", "Smith" )
//                            .build();
//        System.out.printf(spacingPattern, "Ex1  SimpleCriteria", "String", cf.toString() );
//
//        SimpleCriteriaObject simpleCriteriaObject = new SimpleCriteriaObject( "names", "lastName", "Smith" );
//        cf = new CriteriaFilter.Builder()
//                .withSimpleCriteria(simpleCriteriaObject)
//                .build();
//        System.out.printf(spacingPattern, "Ex2  SimpleCriteriaObject", "Link", cf.toString() );
//
//        simpleCriteriaObject = new SimpleCriteriaObject( "names", "lastName", "Smith" );
//        SimpleCriteriaObject nestedSimpleCriteria = new SimpleCriteriaObject( "anotherName", simpleCriteriaObject);
//        SimpleCriteriaObject nestedSimpleCriteria2 = new SimpleCriteriaObject( "someName", nestedSimpleCriteria );
//        cf = new CriteriaFilter.Builder()
//                .withSimpleCriteria(nestedSimpleCriteria2)
//                .build();
//        System.out.printf(spacingPattern, "Ex3  Nested SimpleCriteriaObjects", "Object.Link", cf.toString() );
//
//        MultiCriteriaObject mco = new MultiCriteriaObject( "names", "firstName", "John" );
//        mco.addSimpleCriteria( new SimpleCriteria("lastName", "Smith") );
//        cf = new CriteriaFilter.Builder()
//                .withSimpleCriteria(mco)
//                .build();
//        System.out.printf(spacingPattern, "Ex4  MultiCriteriaObject: ", "Object1.Number and Object1.Number", cf.toString() );
//
//        SimpleCriteriaValueArray scva = new SimpleCriteriaValueArray( "fruit", "apples" );
//        scva.addValue( "oranges" );
//        scva.addValue( "bananas" );
//        cf = new CriteriaFilter.Builder()
//                .withSimpleCriteria(scva)
//                .build();
//        System.out.printf(spacingPattern, "Ex5  SimpleCriteriaValueArray", "Array-String", cf.toString() );
//
//        SimpleCriteriaArray sca = new SimpleCriteriaArray( "food", "apples", "fruit" );
//        sca.addSimpleCriteria( new SimpleCriteria("meat", "hamburger") );
//        sca.addSimpleCriteria( new SimpleCriteria("vegetable", "potato") );
//        cf = new CriteriaFilter.Builder()
//                .withSimpleCriteria(sca)
//                .build();
//        System.out.printf(spacingPattern, "Ex6  SimpleCriteriaArray", "Array-Link", cf.toString() );
//
//        SimpleCriteriaObject sco = new SimpleCriteriaObject( "breakfast", "pancakes", "syrup" );
//        SimpleCriteriaObjectArray scoa = new SimpleCriteriaObjectArray( "meals", sco );
//        cf = new CriteriaFilter.Builder()
//                .withSimpleCriteria(scoa)
//                .build();
//        System.out.printf(spacingPattern, "Ex7  SimpleCriteriaObjectArray", "Array.Link", cf.toString() );
//
//        sca = new SimpleCriteriaArray( "food", "apples", "fruit" );
//        sca.addSimpleCriteria( new SimpleCriteria("meat", "steak") );
//        sco = new SimpleCriteriaObject( "meals", sca );
//        cf = new CriteriaFilter.Builder()
//                .withSimpleCriteria(sco)
//                .build();
//        System.out.printf(spacingPattern, "Ex8  Nested SimpleCriteriaArray", "Object.Array.String", cf.toString() );
//
//        SimpleCriteriaObject fordSco = new SimpleCriteriaObject( "truck", "F150", "Ford" );
//        SimpleCriteria hondaSc = new SimpleCriteria( "Accord", "Honda" );
//        sca = new SimpleCriteriaArray( "autos", fordSco );
//        sca.addSimpleCriteria( hondaSc );
//        cf = new CriteriaFilter.Builder()
//                .withSimpleCriteria(sca)
//                .build();
//        System.out.printf(spacingPattern, "Ex9  SimpleCriteriaObject/SimpleCriteria Array", "Array1.Link and Array1.String", cf.toString() );
//
//        MultiCriteriaObject namesMultiCO = new MultiCriteriaObject( "lastName", "Smith");
//        namesMultiCO.addSimpleCriteria( new SimpleCriteria("firstName", "John") );
//        MultiCriteriaObject foodMultiCO = new MultiCriteriaObject( "meat", "hamburger");
//        foodMultiCO.addSimpleCriteria( new SimpleCriteria("fruit", "apple") );
//        MultiCriteriaObjectArray mcoa = new MultiCriteriaObjectArray( "stuff", namesMultiCO );
//        mcoa.addSimpleCriteria( foodMultiCO );
//        cf = new CriteriaFilter.Builder()
//                .withSimpleCriteria(mcoa)
//                .build();
//        System.out.printf(spacingPattern, "Ex10 MultiCriteriaObjectArray", "Array1.Enum and Array1.String", cf.toString() );
//
//        SimpleCriteriaObject simpleCO = new SimpleCriteriaObject( "names", "firstName", "John" );
//        SimpleCriteriaObject simpleCOInnerNested = new SimpleCriteriaObject( "innerName", simpleCO );
//        SimpleCriteriaObject simpleCOOuterNested = new SimpleCriteriaObject( "outerName", simpleCOInnerNested );
//        SimpleCriteriaObjectArray simpleCOA = new SimpleCriteriaObjectArray( "myName", simpleCOOuterNested );
//        simpleCOA.addSimpleCriteria( simpleCOOuterNested ); // add it again to have more than 1.
//        cf = new CriteriaFilter.Builder()
//                .withSimpleCriteria(simpleCOA)
//                .build();
//        System.out.printf(spacingPattern, "Ex11 Double Nested SimpleCriteriaObjectArray", "Array.OneOf.OneOf.Link", cf.toString() );
//
//        SimpleCriteriaObject simpleCo = new SimpleCriteriaObject( "names", "firstName", "John" );
//        SimpleCriteriaObject simpleCONested = new SimpleCriteriaObject( "nestedName", simpleCo );
//        SimpleCriteriaObjectArray simpleCOArray = new SimpleCriteriaObjectArray( "myName", simpleCONested );
//        simpleCOArray.addSimpleCriteria( simpleCONested ); // add it again to have more than 1.
//        cf = new CriteriaFilter.Builder()
//                .withSimpleCriteria(simpleCOArray)
//                .build();
//        System.out.printf(spacingPattern, "Ex12 Nested SimpleCriteriaObjectArray", "Array.OneOf.Link", cf.toString() );
//
        CriteriaFilter cf = new SimpleCriteria.Builder()
                            .withMultiCriteriaObjectForArray("lastName", "Smith")
//                            .addSimpleCriteria("firstName", "John")
                            .buildCriteriaFilter();
        System.out.println(cf.toString());
    }

    @Test
    public void namedQueryTests() throws Exception {
//        NamedQuery namedQuery = new SimpleCriteria.Builder()
//                                .withNamedQuery( "searchable", "searchable", "searchThis" );
        NamedQueryFilter namedQuery = new SimpleCriteria.Builder()
                .withNamedQuery( "searchable", "searchable", "searchThis" )
                .buildNamedQueryFilter();
        System.out.println( namedQuery.toString() );

//        NamedQueryObject namedQueryObject = new SimpleCriteria.Builder()
//                                            .withNamedQueryObject("instructor", "instructor", "id", "11111111-1111-1111-1111-111111111111");
        NamedQueryFilter namedQueryObject = new SimpleCriteria.Builder()
                .withNamedQueryObject("instructor", "instructor", "id", "11111111-1111-1111-1111-111111111111")
                .buildNamedQueryFilter();
        System.out.println( namedQueryObject.toString() );

//        NamedQueryCombination namedQueryCombination = new SimpleCriteria.Builder()
//                                                      .withNamedQueryCombination("advancedSearch", "keyword", "someKeyword");
//        namedQueryCombination.addNamedQueryObject("defaultSettings", "id", "11111111-1111-1111-1111-111111111111");
        NamedQueryFilter namedQueryCombination = new SimpleCriteria.Builder()
                .withNamedQueryCombination("advancedSearch", "keyword", "someKeyword")
                .addNamedQueryObject("defaultSettings", "id", "11111111-1111-1111-1111-111111111111")
                .buildNamedQueryFilter();
        System.out.println( namedQueryCombination.toString() );
    }

    @Test
    public void reflectionTest() throws Exception {
        String type = "java.lang.Integer";
        String value = "500";
//        Class intClass = Class.forName( type );
//        System.out.println("IS ASSIGNABLE FROM: " + intClass.isAssignableFrom(value.getClass()));
//        Object obj = intClass.cast( value );
//        System.out.println( "OBJECT CLASS: " + obj.getClass().getSimpleName() );
        Object obj = Class.forName(type).getConstructor(String.class).newInstance(value);
        System.out.println( "OBJECT CLASS: " + obj.getClass().getSimpleName() + ", OBJECT VALUE: " + obj );
    }


    @Test
    public void simpleCriteriaTests2() {
        System.out.println( "UPDATED CRITERIA");
        String spacingPattern = "%-50s%-40s%-45s%n";
        System.out.printf( spacingPattern, "SDK EXAMPLE", "SAMPLE FILTER NAME", "EXAMPLE FILTER" );

        CriteriaFilter cf = new SimpleCriteria.Builder()
                .withSimpleCriteria("lastName", "Smith")
                .buildCriteriaFilter();
        System.out.printf(spacingPattern, "Ex1  SimpleCriteria", "String", cf.toString() );

        cf = new SimpleCriteria.Builder()
                .withSimpleCriteria("lastName", "Smith")
                .buildCriteriaFilter();
        System.out.printf(spacingPattern, "Ex1  SimpleCriteria NE", "String", cf.toString() );

        cf = new SimpleCriteria.Builder()
                .withSimpleCriteriaObject("names", "lastName", "Smith")
                .buildCriteriaFilter();
        System.out.printf(spacingPattern, "Ex2  SimpleCriteriaObject", "Link", cf.toString() );

        cf = new SimpleCriteria.Builder()
                .withSimpleCriteriaObject("names", "lastName", "Smith")
                .buildCriteriaFilter();
        System.out.printf(spacingPattern, "Ex2  SimpleCriteriaObject NE", "Link", cf.toString() );

        cf = new SimpleCriteria.Builder()
                .withSimpleCriteriaObject("names", "lastName", "Smith")
                .nestCriteria("anotherName")
                .nestCriteria("someName")
                .buildCriteriaFilter();
        System.out.printf(spacingPattern, "Ex3  Nested SimpleCriteriaObjects", "Object.Link", cf.toString() );

        cf = new SimpleCriteria.Builder()
                .withSimpleCriteriaObject("names", "lastName", "Smith")
                .nestCriteria("anotherName")
                .nestCriteria("someName")
                .buildCriteriaFilter();
        System.out.printf(spacingPattern, "Ex3  Nested SimpleCriteriaObjects NE", "Object.Link", cf.toString() );

        cf = new SimpleCriteria.Builder()
                .withMultiCriteriaObject("startOn", "year", "2021", true)
                .addNumericSimpleCriteria("month", "08")
                .buildCriteriaFilter();
        System.out.printf(spacingPattern, "Ex4  MultiCriteriaObject", "Object1.Number and Object1.Number", cf.toString() );

        SimpleCriteria sc = new SimpleCriteria.Builder().withSimpleCriteria("month", "08" );
        cf = new SimpleCriteria.Builder()
                .withMultiCriteriaObject("startOn", "year", "2021", true)
                .buildCriteriaFilter();
        System.out.printf(spacingPattern, "Ex4  MultiCriteriaObject NE", "Object1.Number and Object1.Number", cf.toString() );

        cf = new SimpleCriteria.Builder()
                .withSimpleCriteriaValueArray("fruit", "apples")
                .addValue("oranges")
                .addValue("bananas")
                .buildCriteriaFilter();
        System.out.printf(spacingPattern, "Ex5  SimpleCriteriaValueArray", "Array-String", cf.toString() );

        cf = new SimpleCriteria.Builder()
                .withSimpleCriteriaArray("names", "lastName", "Smith")
                .addSimpleCriteria( "firstName", "John")
                .buildCriteriaFilter();
        System.out.printf(spacingPattern, "Ex6  SimpleCriteriaArray", "Array-Link", cf.toString() );

        cf = new SimpleCriteria.Builder()
                .withSimpleCriteriaObjectArray("authors")
                .addSimpleCriteriaObject("person", "id", "11111111-1111-1111-1111-111111111111")
                .buildCriteriaFilter();
        System.out.printf(spacingPattern, "Ex7  SimpleCriteriaObjectArray", "Array.Link", cf.toString() );

        cf = new SimpleCriteria.Builder()
                .withSimpleCriteriaArray("food", "apples", "fruit")
                .addSimpleCriteria("meat", "steak")
                .nestInSimpleCriteriaObject("meals")
                .buildCriteriaFilter();
        System.out.printf(spacingPattern, "Ex8  Nested SimpleCriteriaArray", "Object.Array.String", cf.toString() );

        cf = new SimpleCriteria.Builder()
                .withSimpleCriteriaArray("credentials")
                .addSimpleCriteriaObject("type", "id", "11111111-1111-1111-1111-111111111111")
                .addSimpleCriteria("value", "someValue")
                .buildCriteriaFilter();
        System.out.printf(spacingPattern, "Ex9  SimpleCriteriaObject/SimpleCriteria Array", "Array1.Link and Array1.String", cf.toString() );

        SimpleCriteria.Builder scBuilder = new SimpleCriteria.Builder();
        cf = scBuilder.withMultiCriteriaObjectArray("credentials")
                .addMultiCriteriaObject(scBuilder.withMultiCriteriaObjectForArray("type", "bannerId").addSimpleCriteria("value", "myBannerId"))
                .buildCriteriaFilter();

        System.out.printf(spacingPattern, "Ex10 MultiCriteriaObjectArray", "Array1.Enum and Array1.String", cf.toString() );

        scBuilder = new SimpleCriteria.Builder();
        cf = scBuilder.withSimpleCriteriaObjectArray("solicitors",
                scBuilder.withSimpleCriteriaObject("person", "id", "11111111-1111-1111-1111-111111111111")
                        .nestCriteria("constituent")
                        .nestCriteria("solicitor"))
                .buildCriteriaFilter();
        System.out.printf(spacingPattern, "Ex11 Double Nested SimpleCriteriaObjectArray", "Array.OneOf.OneOf.Link", cf.toString() );

        scBuilder = new SimpleCriteria.Builder();
        SimpleCriteriaObject sco = scBuilder.withSimpleCriteriaObject("names", "firstName", "John")
                .nestCriteria("nestedName");
        cf = scBuilder.withSimpleCriteriaObjectArray("myName", sco)
                .addSimpleCriteria(sco) // add it again to have more than 1.
                .buildCriteriaFilter();
        System.out.printf(spacingPattern, "Ex12 Nested SimpleCriteriaObjectArray", "Array.OneOf.Link", cf.toString() );


        // TESTING:  UNSUPPORTED FILTER STRUCTURE:  ?criteria={"ethos":{"resources":["persons","organizations"]}}
        scBuilder = new SimpleCriteria.Builder();
        SimpleCriteriaValueArray scva = scBuilder.withSimpleCriteriaValueArray("resources", "persons")
                .addValue("organizations");
        cf = scBuilder.withSimpleCriteriaObject("ethos", scva)
                      .buildCriteriaFilter();
        System.out.printf(spacingPattern, "Ex13 Nested SimpleCriteriaValueArray", "Array.OneOf.Link", cf.toString() );

        cf = scBuilder.withSimpleCriteriaObject("ethos",
             scBuilder.withSimpleCriteriaValueArray("resources", "persons")
                      .addValue("organizations"))
                      .buildCriteriaFilter();
        System.out.printf(spacingPattern, "Ex13 Nested SimpleCriteriaValueArray", "Array.OneOf.Link", cf.toString() );

        cf = scBuilder.withSimpleCriteriaObject("ethos",
                scBuilder.withMultiCriteriaObject("startOn", "year", "2021", true)
                         .addNumericSimpleCriteria("month", "08"))
             .buildCriteriaFilter();
        System.out.printf(spacingPattern, "Ex13 Nested SimpleCriteriaValueArray", "Array.OneOf.Link", cf.toString() );

        // TESTING NAMED QUERY OBJECT COMBINATION ARRAY:
        NamedQueryFilter nqf = new SimpleCriteria.Builder()
                               .withNamedQueryObjectArrayCombination("registrationStatusesByAcademicPeriod", "academicPeriod", "id", "11111111-1111-1111-1111-111111111111")
                               .withArrayLabel("statuses")
                               .addToNamedQueryObjectArray("detail", "id", "22222222-2222-2222-2222-222222222222")
//                               .addToNamedQueryObjectArray("detail", "id", "33333333-3333-3333-3333-333333333333")
                               .buildNamedQueryFilter();
        System.out.printf(spacingPattern, "Ex14 NamedQueryObjectArrayCombination", "NamedQuery", nqf.toString() );
    }

    @Test
    public void dateTest() {
        String someDatetime = "2021-04-02T00:00:00Z";
//        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
//        LocalDateTime localDateTime = LocalDateTime.parse( someDate );
//        LocalDate localDate = localDateTime.toLocalDate();
        Instant instant = Instant.parse( someDatetime );
        LocalDate localDate = LocalDate.ofInstant( instant, ZoneId.of(ZoneOffset.UTC.getId()) );
//        LocalDate localDate = LocalDate.ofInstant( instant, ZoneId.systemDefault() );
        System.out.println( "DATE: " + localDate.toString() );

//        String someDate = "2021-04-01";
//        instant = DateTimeFormatter.ISO_LOCAL_DATE.parse(someDate);
//        LocalDateTime localDateTime = LocalDateTime.ofInstant( instant, ZoneId.of(ZoneOffset.UTC.getId()) );
//        System.out.println( "DATETIME: " + localDateTime.toString() );
    }

    @Test
    public void encodeTest() throws Exception {
        String jsonStr = "{\"id\":\"210009107\"}";
        System.out.println( jsonStr );
        jsonStr = URLEncoder.encode( jsonStr, "UTF-8");
        System.out.println( jsonStr );
    }

}