/*
 * ******************************************************************************
 *   Copyright 2021 Ellucian Company L.P. and its affiliates.
 * ******************************************************************************
 */
package com.ellucian.ethos.integration.client.proxy.filter;

/**
 * SimpleCriteria class used when building a CriteriaFilter or NamedQueryFilter.  Should be used when building criteria filter
 * requests for Ethos resources using version 8 or later.  Contains an internal Builder class following a variation of the builder pattern
 * used for building the various criteria objects.  Contains simple key and value properties where the key is the JSON
 * label and the value is the value to filter by.
 * <p>
 * The following is an example of using this class to build a CriteriaFilter which generates the following JSON filter syntax:
 * <pre>
 *     CriteriaFilter cf = new SimpleCriteria.Builder()
 *                        .withSimpleCriteria("lastName", "Smith")
 *                        .buildCriteriaFilter();
 *    System.out.println( "CriteriaFilter syntax: " + cf.toString() );
 *    // Output is:  CriteriaFilter syntax: ?criteria={"lastName":"Smith"}
 * </pre>
 * @since 0.3.0
 * @author David Kumar
 */
public class SimpleCriteria {

    // ==========================================================================
    // Attributes
    // ==========================================================================
    /** The key of the JSON label for the criteria. */
    protected String key;

    /** The value to filter by. */
    protected String value;

    /**
     * Indicates if the value is numeric or not.  True if numeric, false it not.  A numeric value will not have quotes
     * around it in the JSON filter syntax this generates.
     */
    protected boolean valueIsNumeric;

    // ==========================================================================
    // Methods
    // ==========================================================================
    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * No-arg constructor.  Defaults the valueIsNumeric indicator to false.
     */
    protected SimpleCriteria() {
        this.valueIsNumeric = false;
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Instantiates an instance of this class with the given criteria key and value.
     * Defaults the valueIsNumeric indicator to false.
     * @param criteriaKey The criteria key (JSON label) for the criteria.
     * @param criteriaValue The value to filter by.
     */
    protected SimpleCriteria( String criteriaKey, String criteriaValue ) {
        this( criteriaKey, criteriaValue, false );
    }

    /**
     * <b>Intended to be used internally by the SDK.</b>
     * <p>
     * Instantiates an instance of this class with the given criteria key and value.
     * @param criteriaKey The criteria key (JSON label) for the criteria.
     * @param criteriaValue The value to filter by.
     * @param valueIsNumeric If true, the JSON syntax for this criteria will not contain quotes, if false it will.
     */
    protected SimpleCriteria( String criteriaKey, String criteriaValue, boolean valueIsNumeric ) {
        this();
        this.key = criteriaKey;
        this.value = criteriaValue;
        this.valueIsNumeric = valueIsNumeric;
    }

    /**
     * Builder class used to build the various criteria objects for building a {@link CriteriaFilter}.
     * This Builder class should be used for building criteria objects rather than instantiating those classes directly.
     */
    public static class Builder {

        /**
         * No-arg constructor for easy access in instantiating this class.
         */
        public Builder() {}

        /**
         * Builds an instance of {@link SimpleCriteria this} class with the given criteria.
         * <p>
         * The following is an example of the criteria JSON syntax generated from a CriteriaFilter using this class:
         * <code>?criteria={"lastName":"Smith"}</code>
         * <p>
         * The following is an example of how to use this method in building a CriteriaFilter for that syntax:
         * <pre>
         *     CriteriaFilter cf = new SimpleCriteria.Builder()
         *                             .withSimpleCriteria("lastName", "Smith")
         *                             .buildCriteriaFilter();
         * </pre>
         * @param criteriaKey The key (JSON label) for the given criteria value.
         * @param criteriaValue The value of the criteria to filter by.
         * @return A {@link SimpleCriteria} object containing the given criteria, which can then be used for building a CriteriaFilter.
         * @throws IllegalArgumentException Thrown if the given criteriaKey or criteriaValue is null or empty.
         */
        public SimpleCriteria withSimpleCriteria( String criteriaKey, String criteriaValue ) {
            validateInputCriteria( criteriaKey, criteriaValue, SimpleCriteria.class.getSimpleName() );
            return new SimpleCriteria( criteriaKey, criteriaValue );
        }

        /**
         * Builds an instance of the {@link SimpleCriteriaObject} class with the given criteria.
         * <p>
         * The following is an example of the criteria JSON syntax generated from a CriteriaFilter using the SimpleCriteriaObject class.
         * The parameters in this method are notated with the filter syntax as below:
         * <pre>
         *
         *                 label   criteriaKey  criteriaValue
         *                  / \      /    \     /  \
         *                 /   \    /      \   /    \
         *     ?criteria={"names":{"lastName":"Smith"}}
         * </pre>
         * <p>
         * The following is an example of how to use this method in building a CriteriaFilter for that syntax:
         * <pre>
         *     CriteriaFilter cf = new SimpleCriteria.Builder()
         *                         .withSimpleCriteriaObject("names", "lastName", "Smith")
         *                         .buildCriteriaFilter();
         * </pre>
         * @param label The JSON label for the generated criteria object.
         * @param criteriaKey The key (JSON label) for the given criteria value.
         * @param criteriaValue The value of the criteria to filter by.
         * @return A {@link SimpleCriteriaObject} object containing the given criteria, which can then be used for building a CriteriaFilter.
         * @throws IllegalArgumentException Thrown if the given parameters are null or empty.
         */
        public SimpleCriteriaObject withSimpleCriteriaObject( String label, String criteriaKey, String criteriaValue ) {
            validateInputCriteria( label, SimpleCriteriaObject.class.getSimpleName() );
            validateInputCriteria( criteriaKey, criteriaValue, SimpleCriteriaObject.class.getSimpleName() );
            return new SimpleCriteriaObject( label, criteriaKey, criteriaValue );
        }


        /**
         * Builds an instance of the {@link SimpleCriteriaObject} class, nesting the given simpleCriteria within the given label.
         * <p>
         * The following is an example of the criteria JSON syntax generated from a CriteriaFilter using this method.
         * The simpleCriteria parameter can be an instance of any criteria subclass extending SimpleCriteria.
         * The parameters in this method are notated with the filter syntax as below:
         * <pre>
         *
         *                 label                simpleCriteria (happens to be a SimpleCriteriaValueArray)
         *                  / \      _________________/\_________________
         *                 /   \    /                                    \
         *     ?criteria={"ethos":{"resources":["persons","organizations"]}}
         * </pre>
         * <p>
         * The following is an example of how to use this method in building a CriteriaFilter for the above syntax:
         * <pre>
         *     SimpleCriteria.Builder scBuilder = new SimpleCriteria.Builder();
         *     CriteriaFilter cf = scBuilder.withSimpleCriteriaObject("ethos",
         *                         scBuilder.withSimpleCriteriaValueArray("resources", "persons")
         *                                  .addValue("organizations"))
         *                         .buildCriteriaFilter();
         * </pre>
         *
         * Another example using this method with an instance of MultiCriteriaObject for the simpleCriteria param instance:
         * <pre>
         *
         *                 label            simpleCriteria (happens to be a MultiCriteriaObject)
         *                  / \      _____________/\_________________
         *                 /   \    /                                \
         *     ?criteria={"ethos":{"startOn":{"year":2021,"month":08}}}
         * </pre>
         * The following is an example of how to use this method in building a CriteriaFilter for the above syntax using
         * a MultiCriteriaObject:
         * <pre>
         *     SimpleCriteria.Builder scBuilder = new SimpleCriteria.Builder();
         *     CriteriaFilter cf = scBuilder.withSimpleCriteriaObject("ethos",
         *                         scBuilder.withMultiCriteriaObject("startOn", "year", "2021", true)
         *                                  .addNumericSimpleCriteria("month", "08"))
         *                         .buildCriteriaFilter();
         * </pre>
         * @param label The JSON label for the generated criteria object.
         * @param simpleCriteria An instance of a SimpleCriteria, can be a criteria subclass of SimpleCriteria.
         * @return A {@link SimpleCriteriaObject} object containing the given criteria, which can then be used for building a CriteriaFilter.
         * @throws IllegalArgumentException Thrown if the given parameters are null or empty.
         */
        public SimpleCriteriaObject withSimpleCriteriaObject( String label, SimpleCriteria simpleCriteria ) {
            validateInputCriteria( label, SimpleCriteriaObject.class.getSimpleName() );
            validateInputCriteria( simpleCriteria, SimpleCriteriaObject.class.getSimpleName() );
            return new SimpleCriteriaObject( label, simpleCriteria );
        }

        /**
         * Intended to be used when adding criteria to a MultiCriteriaObjectArray, and should not be used standalone
         * because the MultiCriteriaObject built by this method will not contain a label.
         * Please use {@link SimpleCriteria.Builder#withMultiCriteriaObject(String, String, String, boolean)} when building
         * a MultiCriteriaObject.
         * This method builds an instance of the {@link MultiCriteriaObject} class with the given criteria for use within a
         * MultiCriteriaObjectArray.
         * <p>
         * Please see {@link SimpleCriteria.Builder#withMultiCriteriaObjectArray(String)} for an example of how to use this method.
         * @param criteriaKey The key (JSON label) for the given criteria value.
         * @param criteriaValue The value of the criteria to filter by.
         * @return A {@link MultiCriteriaObject} object containing the given criteria, for use within a MultiCriteriaObjectArray.
         * @throws IllegalArgumentException Thrown if the given parameters are null or empty.
         */
        public MultiCriteriaObject withMultiCriteriaObjectForArray(String criteriaKey, String criteriaValue ) {
            return withMultiCriteriaObject( null, criteriaKey, criteriaValue, false );
        }

        /**
         * Builds an instance of the {@link MultiCriteriaObject} class with the given criteria.
         * <p>
         * The following is an example of the criteria JSON syntax generated from a CriteriaFilter using the MultiCriteriaObject class.
         * The parameters in this method are notated with the filter syntax as below:
         * <pre>
         *
         *                 label   criteriaKey       criteriaValue
         *                  /  \      /  \                /\
         *                 /    \    /    \              /  \
         *     ?criteria={"startOn":{"year":2021,"month":08}}
         * </pre>
         * <p>
         * In the syntax example above, 2021 would also be another criteriaValue, and "month" would be another criteriaKey.
         * <p>
         * The following is an example of how to use this method in building a CriteriaFilter for that syntax:
         * <pre>
         *     CriteriaFilter cf = new SimpleCriteria.Builder()
         *                         .withMultiCriteriaObject("startOn", "year", "2021", true)
         *                         .addNumericSimpleCriteria("month", "08")
         *                         .buildCriteriaFilter();
         * </pre>
         * @param label The JSON label for the generated criteria object.
         * @param criteriaKey The key (JSON label) for the given criteria value.
         * @param criteriaValue The value of the criteria to filter by.
         * @param valueIsNumeric Indicates whether the value in the generated JSON syntax is numeric (true) or not (false).
         *                       If numeric, there will not be quotes around the value.
         * @return A {@link SimpleCriteriaObject} object containing the given criteria, which can then be used for building a CriteriaFilter.
         * @throws IllegalArgumentException Thrown if the given parameters are null or empty.
         */
        public MultiCriteriaObject withMultiCriteriaObject(String label, String criteriaKey, String criteriaValue, boolean valueIsNumeric ) {
            // For a MultiCriteriaObject, the label can be null, so NOT validating the label here.
            validateInputCriteria( criteriaKey, criteriaValue, MultiCriteriaObject.class.getSimpleName() );
            return new MultiCriteriaObject( label, criteriaKey, criteriaValue, valueIsNumeric );
        }

        /**
         * Builds an instance of the {@link SimpleCriteriaValueArray} class with the given criteria.
         * <p>
         * The following is an example of the criteria JSON syntax generated from a CriteriaFilter using the SimpleCriteriaValueArray class.
         * The parameters in this method are notated with the filter syntax as below:
         * <pre>
         *                 criteriaKey             criteriaValues
         *                  /       \     _______________/\________________
         *                 /         \   /                                 \
         *     ?criteria={"credentials":["bannerId","colleagueId","anotherId"]}
         * </pre>
         * <p>
         * In the syntax example above, "bannerId", "colleagueId", and "anotherId" are each a criteriaValue.
         * <p>
         * The following is an example of how to use this method in building a CriteriaFilter for that syntax:
         * <pre>
         *     CriteriaFilter cf = new SimpleCriteria.Builder()
         *                         .withSimpleCriteriaValueArray("credentials", "bannerId")
         *                         .addValue("colleagueId")
         *                         .addValue("anotherId")
         *                         .buildCriteriaFilter();
         * </pre>
         * @param criteriaKey The key (JSON label) for the given criteria value array.
         * @param criteriaValue The value of the criteria to filter by, added to the array.
         * @return A {@link SimpleCriteriaValueArray} object containing the given criteria, which can then be used for building a CriteriaFilter.
         * @throws IllegalArgumentException Thrown if the given parameters are null or empty.
         */
        public SimpleCriteriaValueArray withSimpleCriteriaValueArray( String criteriaKey, String criteriaValue ) {
            validateInputCriteria( criteriaKey, criteriaValue, SimpleCriteriaValueArray.class.getSimpleName() );
            return new SimpleCriteriaValueArray( criteriaKey, criteriaValue );
        }

        /**
         * Builds an instance of the {@link SimpleCriteriaArray} class with the given criteria.  For use when a SimpleCriteriaArray
         * needs to contain a SimpleCriteriaObject, which can be added after specifying the label with this method.
         * <p>
         * The following is an example of the criteria JSON syntax generated from a CriteriaFilter using the SimpleCriteriaArray class.
         * The parameters in this method are notated with the filter syntax as below:
         * <pre>
         *                   label                      SimpleCriteriaObject                        SimpleCriteria
         *                  ___/\___       ______________________/\_________________________      ________/\________
         *                 /        \     /                                                 \    /                  \
         *     ?criteria={"credentials":[{"type":{"id":"11111111-1111-1111-1111-111111111111"}},{"value":"someValue"}]}
         * </pre>
         * <p>
         * The following is an example of how to use this method in building a CriteriaFilter for that syntax:
         * <pre>
         *     CriteriaFilter cf = new SimpleCriteria.Builder()
         *                         .withSimpleCriteriaArray("credentials")
         *                         .addSimpleCriteriaObject("type", "id", "11111111-1111-1111-1111-111111111111")
         *                         .addSimpleCriteria("value", "someValue")
         *                         .buildCriteriaFilter();
         * </pre>
         * @param label The JSON label for the given SimpleCriteriaArray.
         * @return A {@link SimpleCriteriaArray} object containing the given criteria, which can then be used for building a CriteriaFilter.
         * @throws IllegalArgumentException Thrown if the given parameters are null or empty.
         */
        public SimpleCriteriaArray withSimpleCriteriaArray( String label ) {
            validateInputCriteria( label, SimpleCriteriaArray.class.getSimpleName() );
            return new SimpleCriteriaArray( label );
        }

        /**
         * Builds an instance of the {@link SimpleCriteriaArray} class with the given criteria.  For use when a SimpleCriteriaArray
         * needs to contain SimpleCriteria, which can be added after specifying the label with the initial criteria.
         * <p>
         * The following is an example of the criteria JSON syntax generated from a CriteriaFilter using the SimpleCriteriaArray class.
         * The parameters in this method are notated with the filter syntax as below:
         * <pre>
         *                 label   criteriaKey  criteriaValue
         *                  /  \      /    \     /  \
         *                 /    \    /      \   /    \
         *     ?criteria={"names":[{"lastName":"Smith"}]}
         * </pre>
         * <p>
         * The following is an example of how to use this method in building a CriteriaFilter for that syntax:
         * <pre>
         *     CriteriaFilter cf = new SimpleCriteria.Builder()
         *                         .withSimpleCriteriaArray("names", "lastName", "Smith")
         *                         .buildCriteriaFilter();
         * </pre>
         * @param label The JSON label for the given SimpleCriteriaArray.
         * @param criteriaKey The JSON key (label) for the given criteriaValue.
         * @param criteriaValue The value of the criteria to filter by.
         * @return A {@link SimpleCriteriaArray} object containing the given criteria, which can then be used for building a CriteriaFilter.
         * @throws IllegalArgumentException Thrown if the given parameters are null or empty.
         */
        public SimpleCriteriaArray withSimpleCriteriaArray( String label, String criteriaKey, String criteriaValue ) {
            validateInputCriteria( label, SimpleCriteriaArray.class.getSimpleName() );
            validateInputCriteria( criteriaKey, criteriaValue, SimpleCriteriaArray.class.getSimpleName() );
            return new SimpleCriteriaArray( label, criteriaKey, criteriaValue );
        }

        /**
         * Builds an instance of the {@link SimpleCriteriaObjectArray} class with the given criteria.  For use when building
         * a SimpleCriteriaObjectArray with the given label, from which SimpleCriteriaObjects can be added.
         * <p>
         * The following is an example of the criteria JSON syntax generated from a CriteriaFilter using the SimpleCriteriaObjectArray class.
         * The parameters in this method are notated with the filter syntax as below:
         * <pre>
         *                 label                      SimpleCriteriaObject
         *                  /  \        _______________________/\__________________________
         *                 /    \     /                                                    \
         *     ?criteria={"authors":[{"person":{"id":"11111111-1111-1111-1111-111111111111"}}]}
         * </pre>
         * <p>
         * The following is an example of how to use this method in building a CriteriaFilter for that syntax:
         * <pre>
         *     CriteriaFilter cf = new SimpleCriteria.Builder()
         *                        .withSimpleCriteriaObjectArray("authors")
         *                        .addSimpleCriteriaObject("person", "id", "11111111-1111-1111-1111-111111111111")
         *                        .buildCriteriaFilter();
         * </pre>
         * @param label The JSON label for the given SimpleCriteriaObjectArray.
         * @return A {@link SimpleCriteriaObjectArray} object containing the given label, which can then be used for building a CriteriaFilter.
         * @throws IllegalArgumentException Thrown if the given parameters are null or empty.
         */
        public SimpleCriteriaObjectArray withSimpleCriteriaObjectArray( String label ) {
            validateInputCriteria( label, SimpleCriteriaObjectArray.class.getSimpleName() );
            return new SimpleCriteriaObjectArray( label );
        }

        /**
         * Builds an instance of the {@link SimpleCriteriaObjectArray} class with the given criteria.  For use when building
         * a SimpleCriteriaObjectArray with the given label and SimpleCriteriaObject.
         * <p>
         * The following is an example of the criteria JSON syntax generated from a CriteriaFilter using the SimpleCriteriaObjectArray class
         * with nested SimpleCriteriaObjects.
         * The parameters in this method are notated with the filter syntax as below:
         * <pre>
         *                    label      Nested Label   Nested Label                    SimpleCriteriaObject
         *                  /      \       /      \     /       \        _______________________/\__________________________
         *                 /        \     /        \   /         \     /                                                    \
         *     ?criteria={"solicitors":[{"solicitor":{"constituent":{"person":{"id":"11111111-1111-1111-1111-111111111111"}}}}]}
         * </pre>
         * <p>
         * The following is an example of how to use this method in building a CriteriaFilter for that syntax:
         * <pre>
         *     SimpleCriteria.Builder scBuilder = new SimpleCriteria.Builder();
         *     CriteriaFilter cf = scBuilder.withSimpleCriteriaObjectArray("solicitors",
         *                         scBuilder.withSimpleCriteriaObject("person", "id", "11111111-1111-1111-1111-111111111111")
         *                         .nestCriteria("constituent")
         *                         .nestCriteria("solicitor"))
         *                         .buildCriteriaFilter();
         * </pre>
         * @param label The JSON label for the given SimpleCriteriaObjectArray.
         * @param simpleCriteriaObject A SimpleCriteriaObject to initially include in the array.
         * @return A {@link SimpleCriteriaObjectArray} object containing the given label and simpleCriteriaObject, which can then be used for building a CriteriaFilter.
         * @throws IllegalArgumentException Thrown if the given parameters are null or empty.
         */
        public SimpleCriteriaObjectArray withSimpleCriteriaObjectArray( String label, SimpleCriteriaObject simpleCriteriaObject ) {
            validateInputCriteria( label, SimpleCriteriaObjectArray.class.getSimpleName() );
            validateInputCriteria( simpleCriteriaObject, SimpleCriteriaObjectArray.class.getSimpleName() );
            return new SimpleCriteriaObjectArray( label, simpleCriteriaObject );
        }

        /**
         * Builds an instance of the {@link MultiCriteriaObjectArray} class with the given criteria.  For use when building
         * a MultiCriteriaObjectArray with the given label, from which MultiCriteriaObjects can be added.
         * <p>
         * The following is an example of the criteria JSON syntax generated from a CriteriaFilter using the MultiCriteriaObjectArray class.
         * The parameters in this method are notated with the filter syntax as below:
         * <pre>
         *                    label                 MultiCriteriaObject
         *                  /       \       ________________/\__________________
         *                 /         \    /                                     \
         *     ?criteria={"credentials":[{"type":"bannerId","value":"myBannerId"}]}
         * </pre>
         * <p>
         * The following is an example of how to use this method in building a CriteriaFilter for that syntax:
         * <pre>
         *     SimpleCriteria.Builder scBuilder = new SimpleCriteria.Builder();
         *     CriteriaFilter cf = scBuilder.withMultiCriteriaObjectArray("credentials")
         *                         .addMultiCriteriaObject(scBuilder.withMultiCriteriaObjectForArray("type", "bannerId").addSimpleCriteria("value", "myBannerId"))
         *                         .buildCriteriaFilter();
         * </pre>
         * @param label The JSON label for the given SimpleCriteriaObjectArray.
         * @return A {@link MultiCriteriaObjectArray} object containing the given label, which can then be used for building a CriteriaFilter.
         * @throws IllegalArgumentException Thrown if the given parameters are null or empty.
         */
        public MultiCriteriaObjectArray withMultiCriteriaObjectArray( String label ) {
            validateInputCriteria( label, MultiCriteriaObjectArray.class.getSimpleName() );
            return new MultiCriteriaObjectArray( label );
        }

        /**
         * Builds an instance of the {@link NamedQuery} class with the given criteria.  For use when making a filter request
         * using a named query filter.
         * <p>
         * The following is an example of the criteria JSON syntax generated from a NamedQueryFilter using the NamedQuery class.
         * The parameters in this method are notated with the filter syntax as below:
         * <pre>
         *        queryName        queryKey     queryValue
         *       /         \      /        \      /   \
         *      /           \    /          \    /     \
         *     ?keywordSearch={"keywordSearch": "Culture"}
         * </pre>
         * <p>
         * The following is an example of how to use this method in building a NamedQueryFilter for that syntax:
         * <pre>
         *     NamedQueryFilter namedQueryFilter = new SimpleCriteria.Builder()
         *                                         .withNamedQuery("keywordSearch", "keywordSearch", "Culture")
         *                                         .buildNamedQueryFilter();
         * </pre>
         * @param queryName The named query name used to build the NamedQueryFilter.
         * @param queryKey The JSON label of the named query.
         * @param queryValue The (JSON) criteria value to query by.
         * @return A {@link NamedQuery} object containing the given query criteria, which can then be used for building a NamedQueryFilter.
         * @throws IllegalArgumentException Thrown if the given parameters are null or empty.
         */
        public NamedQuery withNamedQuery( String queryName, String queryKey, String queryValue ) {
            validateInputCriteria( queryName, NamedQuery.class.getSimpleName() );
            validateInputCriteria( queryKey, queryValue, NamedQuery.class.getSimpleName() );
            return new NamedQuery( queryName, queryKey, queryValue );
        }

        /**
         * Builds an instance of the {@link NamedQueryObject} class with the given criteria.  For use when making a filter request
         * using a named query filter.  Supports the structure of a basic JSON object within the context of a named query.
         * <p>
         * The following is an example of the criteria JSON syntax generated from a NamedQueryFilter using the NamedQueryObject class.
         * The parameters in this method are notated with the filter syntax as below:
         * <pre>
         *       queryName    queryLabel  queryKey              queryValue
         *       /      \      /     \      / \    ________________/\________________
         *      /        \    /       \     / \   /                                  \
         *     ?instructor={"instructor": {"id": "11111111-1111-1111-1111-111111111111"}}
         * </pre>
         * <p>
         * The following is an example of how to use this method in building a NamedQueryFilter for that syntax:
         * <pre>
         *     NamedQueryFilter namedQueryFilter = new SimpleCriteria.Builder()
         *                                         .withNamedQueryObject("instructor", "instructor", "id", "11111111-1111-1111-1111-111111111111")
         *                                         .buildNamedQueryFilter();
         * </pre>
         * @param queryName The named query name used to build the NamedQueryFilter.
         * @param queryLabel The JSON label of the named query object.
         * @param queryKey The JSON label of the named query.
         * @param queryValue The (JSON) criteria value to query by.
         * @return A {@link NamedQueryObject} object containing the given query criteria, which can then be used for building a NamedQueryFilter.
         * @throws IllegalArgumentException Thrown if the given parameters are null or empty.
         */
        public NamedQueryObject withNamedQueryObject( String queryName, String queryLabel, String queryKey, String queryValue ) {
            validateInputCriteria( queryName, NamedQueryObject.class.getSimpleName() );
            validateInputCriteria( queryLabel, NamedQueryObject.class.getSimpleName() );
            validateInputCriteria( queryKey, queryValue, NamedQueryObject.class.getSimpleName() );
            return new NamedQueryObject( queryName, queryLabel, queryKey, queryValue );
        }

        /**
         * Builds an instance of the {@link NamedQueryCombination} class with the given criteria.  For use when making a filter request
         * using a named query filter.  Supports the structure of a combined NamedQuery and NamedQueryObject.
         * <p>
         * The following is an example of the criteria JSON syntax generated from a NamedQueryFilter using the NamedQueryCombination class.
         * The parameters in this method are notated with the filter syntax as below:
         * <pre>
         *                                              NamedQueryCombination
         *             __________________________________________/\___________________________________________
         *            /                                                                                       \
         *           /       NamedQuery                                       NamedQueryObject                 \
         *          /___________/\______________            _________________________/\_________________________\
         *         /                            \          /                                                     \
         *        /queryName     queryKey  queryValue     / queryLabel  queryKey              queryValue          \
         *       /          \     /    \      /  \       /____/\_____     / \     ________________/\_______________\
         *      /            \   /      \    /    \     /            \    / \    /                                  \
         *     ?advancedSearch={"keyword": "Culture", "defaultSettings": {"id": "11111111-1111-1111-1111-111111111111"}}
         * </pre>
         * <p>
         * The following is an example of how to use this method in building a NamedQueryFilter for that syntax:
         * <pre>
         *     NamedQueryFilter namedQueryFilter = new SimpleCriteria.Builder()
         *                                         .withNamedQueryCombination("advancedSearch", "keyword", "Culture")
         *                                         .addNamedQueryObject("defaultSettings", "id", "11111111-1111-1111-1111-111111111111")
         *                                         .buildNamedQueryFilter();
         * </pre>
         * @param queryName The named query name used to build the NamedQueryFilter.
         * @param queryKey The JSON label of the named query.
         * @param queryValue The (JSON) criteria value to query by.
         * @return A {@link NamedQueryCombination} object containing the given query criteria, which can then be used for adding a
         *                                         NamedQueryObject and building a NamedQueryFilter.
         * @throws IllegalArgumentException Thrown if the given parameters are null or empty.
         */
        public NamedQueryCombination withNamedQueryCombination( String queryName, String queryKey, String queryValue ) {
            validateInputCriteria( queryName, NamedQuery.class.getSimpleName() );
            validateInputCriteria( queryKey, queryValue, NamedQuery.class.getSimpleName() );
            return new NamedQueryCombination( queryName, queryKey, queryValue );
        }

        /**
         * Builds an instance of the {@link NamedQueryObjectArrayCombination} class with the given criteria.  For use when making a filter request
         * using a named query filter.  Supports the structure of a combined NamedQueryObject and NamedQueryObject array.
         * <p>
         * The following is an example of the criteria JSON syntax generated from a NamedQueryFilter using the NamedQueryObjectArrayCombination class.
         * The parameters in this method are notated with the filter syntax as below:
         * <pre>
         *                                                                        NamedQueryObjectArrayCombination
         *             __________________________________________________________________________/\_______________________________________________________________________________
         *            /                                                                                                                                                           \
         *           /                                 NamedQueryObject                                                                          Array of NamedQueryObjects        \
         *          /________________________________________/\_______________________________________________                        _______________________/\_____________________\
         *         /                                                                                          \                      /                                               \
         *        /           queryName                  queryLabel   queryKey            queryValue           \     arrayLabel  queryLabel  queryKey           queryValue            \
         *       /_______________/\________________    _____/\______     /\    _______________/\________________\      /    \      /    \    /\    ________________/\__________________\
         *      /                                  \  /             \   /  \  /                                  \    /      \    /      \  /  \  /                                     \
         *     ?registrationStatusesByAcademicPeriod={"academicPeriod":{"id":"11111111-1111-1111-1111-111111111111"},"statuses":[{"detail":{"id":"22222222-2222-2222-2222-222222222222"}}]}
         * </pre>
         * <p>
         * The following is an example of how to use this method in building a NamedQueryFilter for that syntax:
         * <pre>
         *     NamedQueryFilter nqf = new SimpleCriteria.Builder()
         *                                .withNamedQueryObjectArrayCombination("registrationStatusesByAcademicPeriod", "academicPeriod", "id", "11111111-1111-1111-1111-111111111111")
         *                                .withArrayLabel("statuses")
         *                                .addToNamedQueryObjectArray("detail", "id", "22222222-2222-2222-2222-222222222222")
         *                                .buildNamedQueryFilter();
         * </pre>
         * @param queryName The named query name used to build the NamedQueryFilter.
         * @param queryLabel The JSON label of the named query object.
         * @param queryKey The JSON label of the named query.
         * @param queryValue The (JSON) criteria value to query by.
         * @return A {@link NamedQueryObjectArrayCombination} object containing the given query criteria, which can then be used for building a NamedQueryFilter.
         * @throws IllegalArgumentException Thrown if the given parameters are null or empty.
         */
        public NamedQueryObjectArrayCombination withNamedQueryObjectArrayCombination( String queryName, String queryLabel, String queryKey, String queryValue ) {
            validateInputCriteria( queryName, NamedQueryObjectArrayCombination.class.getSimpleName() );
            validateInputCriteria( queryLabel, NamedQueryObjectArrayCombination.class.getSimpleName() );
            validateInputCriteria( queryKey, queryValue, NamedQueryObjectArrayCombination.class.getSimpleName() );
            return new NamedQueryObjectArrayCombination( queryName, queryLabel, queryKey, queryValue );
        }

        /**
         * <b>Intended to be used internally by the SDK.</b>
         * <p>
         * Validates the given input params to ensure they are not null or blank.  Does nothing if validation is successful.
         * @param label The given label to validate.
         * @param className The given className of the criteria object for the error message in the exception if validation fails.
         * @throws IllegalArgumentException Thrown if the given label is null or blank.
         */
        protected void validateInputCriteria( String label, String className ) {
            if( label == null || label.isBlank() ) {
                throw new IllegalArgumentException( "ERROR: Cannot instantiate " + className + " due to a null or blank property label." );
            }
        }

        /**
         * <b>Intended to be used internally by the SDK.</b>
         * <p>
         * Validates the given input params to ensure they are not null or blank.  Does nothing if validation is successful.
         * @param criteriaKey The given criteriaKey to validate.
         * @param criteriaValue The given criteriaValue to validate.
         * @param className The given className of the criteria object for the error message in the exception if validation fails.
         * @throws IllegalArgumentException Thrown if the given criteriaKey or criteriaValue is null or blank.
         */
        protected void validateInputCriteria( String criteriaKey, String criteriaValue, String className ) {
            if( criteriaKey == null || criteriaKey.isBlank() ) {
                throw new IllegalArgumentException( "ERROR: Cannot instantiate " + className + " due to a null or blank criteriaKey." );
            }
            if( criteriaValue == null || criteriaValue.isBlank() ) {
                throw new IllegalArgumentException( "ERROR: Cannot instantiate "  + className + " due to a null or blank criteriaValue." );
            }
        }

        /**
         * <b>Intended to be used internally by the SDK.</b>
         * <p>
         * Validates the given input param to ensure it is not null.  Does nothing if validation is successful.
         * @param simpleCriteria The given SimpleCriteria to validate.
         * @param className The given className of the criteria object for the error message in the exception if validation fails.
         * @throws IllegalArgumentException Thrown if the given simpleCriteriaObject is null.
         */
        protected void validateInputCriteria( SimpleCriteria simpleCriteria, String className ) {
            if( simpleCriteria == null ) {
                throw new IllegalArgumentException( "ERROR: Cannot instantiate " + className + " due to a null object reference." );
            }
        }

    }

    /**
     * Gets the criteria key.
     * @return The criteria key for this criteria.
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets the value to filter by for this criteria.
     * @return The value to filter by.
     */
    public String getValue() {
        return value;
    }

    /**
     * Builds a CriteriaFilter using this SimpleCriteria instance.  This SimpleCriteria instance could be any of the
     * subclasses extending SimpleCriteria containing any number of additional criteria, and should be built from the SimpleCriteria.Builder class.
     * @return A CriteriaFilter used to generate the appropriate JSON syntax for making a criteria proxy filter request.
     */
    public CriteriaFilter buildCriteriaFilter() {
        return new CriteriaFilter.Builder()
                .withSimpleCriteria(this)
                .build();
    }

    /**
     * Provides a string representation of this criteria, used when building the request URL.
     * @return A string representing the key/value pair of this criteria, in JSON format minus the squirly brackets.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "\"" );
        sb.append( key );
        if (valueIsNumeric) {
            sb.append("\":");
        }
        else {
            sb.append("\":\"");
        }
        sb.append(value);
        if (valueIsNumeric == false) {
            sb.append("\"");
        }
        return sb.toString();
    }


}