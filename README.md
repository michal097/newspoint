# newspoint

Zadanie zostało stworzone jako czyste REST API w celach tesowania zalecam użycie POSTMAN'a 

Endpointy: 

POST
localhost:8080/uploadFile - upload pliku csv z danymi (file jako body)

GET
localhost:8080/allUsersPagination/{page} - pobranie listy użytkowników z paginacją, domyślnie rozmiar strony wynosi 5 (sortowanie po wieku)
localhost:8080/allUsersPagination/{page}/{size} - pobranie listy użytkowników z paginacją z opcją ustalenia rozmiaru strony (sortowanie po wieku)
localhost:8080/getCountUsers - pobranie liczby wszystkich userów w bazie 
localhost:8080/search/{lastName} - wyszukanie użytkownika/użytkowników po nazwisku (w zalezności czy nazwisko się powtarza)

DELETE
localhost:8080/deleteSpecUser/{id} - usunięcie z bazy wybranego użytkownika po id 
localhost:8080/deleteAll - usunięcie wszystkich użytkowników 
