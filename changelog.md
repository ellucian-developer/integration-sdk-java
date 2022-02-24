# Ellucian Ethos Integration Java SDK

## Change Log

Date | Version | Description
---- | ------- | -----------
02-10-2021 | 0.2.0 | Added the `com.ellucian.ethos.integration.notification` package for supporting polling for ChangeNotifications.  Added the `com.ellucian.ethos.integration.service` package supporting the `EthosChangeNotificationService`.
02-23-2021 | 0.3.0 | Updated criteria and named query filter capability to better handle the various combinations of JSON filter syntax and structure.  The `NamedSimpledCritiera` class was removed and replaced with `SimpleCriteriaObject`.  Since this version of the SDK is not yet GA, no deprecation of the removed class was given.
03-15-2021 | 0.4.0 | Fixed a bug where paging for number of rows results were incorrect if the page size specified was greater than the max page size for the given resource.             
03-22-2021 | 0.4.0 | Fixed a bug where paging for num pages or num rows using the EthosFilterQueryClient without a criteria filter, named query, or filter map resulted in a NullPointerException.
04-15-2021 | 0.4.0 | Added criteria filter and named query support for additional filter syntax structures.
09-24-2021 | 0.4.0 | Added criteria filter support for Banner business API requests.