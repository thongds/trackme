
- ## App Architech
    - [x] **MVVM & Android components**
### Database schema :
![alt text](https://serving.photos.photobox.com/6858503798ea2a3c1039a85b5740f4714fb1e0a07476f6b93c3f2004f90ded9d3b30b2d7.jpg)
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
