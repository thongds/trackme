
- ## App Architech
    - [x] **MVVM & Android components**
### Database schema :
![alt text](https://github.com/thongds/trackme/blob/master/app/src/main/java/com/dst/trackme/data/local/schema_update.png?raw=true)
    **Explain**
    
    1. session table
    - save session's infomations
    - valid field => to handle cases which user close app without saving session 
    2. latLnSession table : 
    - save latitude and longitude of session with foreign key sessionId
### Unit Test :
- [x] **Test Database**
- [x] **Test BindingData Format**
- [x] **Test Timer count up**
