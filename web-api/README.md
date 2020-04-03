## Usage

### 1. Register your APP
Register your APP on irext SDK console [irext SDK console](http://irext.net), (You need to register an irext account first)

You need to fetch the package name and SHA1 signature of your APP and fill these information as SDK registration information

While your APP is registered, you can see the APP key and APP secret in your APP list

### 2. Import the SDK
You can either import this project or download the web-api .aar file from [Android SDK](http://irext-lib-releaese.oss-cn-hangzhou.aliyuncs.com/decode/0.1.2/web-api-android-0.1.2.aar) and import to your Android APP project

Add 2 meta-data tags to your AndroidManifest.xml providing APP key and secret get from step 1.
```xml
<meta-data
    android:name="irext_app_key"
    android:value="your app key" />

<meta-data
    android:name="irext_app_secret"
    android:value="your app secret" />
```

### 3. Use the SDK

Import classes:
```java
import net.irext.webapi.model.*;    
import net.irext.webapi.WebAPIs;
```
Get web API instance:
```java
WebAPIs webApis = WebAPIs.getInstance();
```
Sign in for access id and token:
```java
UserApp userApp = webApis.signIn(context);
int id = userApp.getId();
int token = userApp.getToken();
```
Fetch household appliances categories:
```java
List<Category> categories = webApis.listCategories();
```
Fetch brands of a certain category other than STB:
```java
List<Brand> brands = webApis.listBrands(category.getId());
```
Fetch cities (in China) for STB:
```java
List<City> provinces = webApis.listProvinces();
List<City> cities = webApis.listCities(provincePrefix);
```
Fetch STB operators of a certain city:
```java
List<StbOperator>; operators = webApis.listOperators(cityCode);
```
Fetch remote indexes of a certain brand or STB operator:
```java
List<RemoteIndex> remoteIndexes = webApis.listRemoteIndexes(category.getId(), brand.getId(), city.getCode(), operator.getOperator_id());
```
Download IR binary for certain remote index:
```java
InputStream is = webApis.downloadBin(remoteIndex.getRemote_map(), remoteIndex.getId());
```
