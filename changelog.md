# Ellucian Ethos Integration Java SDK

## Change Log

Date | Version | Description
---- | ------- | -----------
FEB 2021 | 0.2.0 | Added the `com.ellucian.ethos.integration.notification` package for supporting polling for ChangeNotifications.  Added the `com.ellucian.ethos.integration.service` package supporting the `EthosChangeNotificationService`.
FEB 2021 | 0.3.0 | Updated criteria and named query filter capability to better handle the various combinations of JSON filter syntax and structure.  The `NamedSimpledCritiera` class was removed and replaced with `SimpleCriteriaObject`.  Since this version of the SDK is not yet GA, no deprecation of the removed class was given.
MAR 2021 | 0.4.0 | Fixed a bug where paging for number of rows results were incorrect if the page size specified was greater than the max page size for the given resource.             
MAR 2021 | 0.4.0 | Fixed a bug where paging for num pages or num rows using the EthosFilterQueryClient without a criteria filter, named query, or filter map resulted in a NullPointerException.
APR 2021 | 0.4.0 | Added criteria filter and named query support for additional filter syntax structures.
SEPT 2021 | 0.4.0 | Added criteria filter support for Banner business API requests.
MAR 2022 | 1.0.0 | Added support in the EthosProxyClient and EthosFilterQueryClient for the associated generic type object library.  This enables requests/responses to be handled with schema-based generated JavaBeans/POJOs.
JUN 2022 | 1.0.0 | Added support in the EthosFilterQueryClient for QAPI POST requests.
NOV 2022 | 1.0.1 | Updated the Jackson Databind dependent library to version 2.13.4.2 to address two vulnerabilities:  https://nvd.nist.gov/vuln/detail/CVE-2022-42003 and https://nvd.nist.gov/vuln/detail/CVE-2022-42004