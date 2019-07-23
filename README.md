#### A.引入:
```
allprojects {
    repositories {
        ...
        maven { url 'https://www.jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.Ct7Liang:ImageSelector:1.2'
}
```

#### B.权限配置(6.0以后注意运行时权限申请)
```
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.CAMERA"/>
```
#### C.FileProvider配置
项目-->res目录下-->新建xml文件夹--创建"file_path.xml"文件(名称随意,与清单文件中保持一致即可)
```
//file_path.xml内容:
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:tools="http://schemas.android.com/tools" tools:ignore="MissingDefaultResource">
    <external-path name="external_files" path="Ct7liang/img_select"/>
</paths>
```
清单文件中:
```
<provider
    android:name="android.support.v4.content.FileProvider"
    android:authorities="项目包名(appId)"
    android:grantUriPermissions="true"
    android:exported="false">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_path" />
</provider>
```

#### D.使用
```
//创建PictureSelector对象
PictureSelector pictureSelector = new PictureSelector(MainActivity.this, "项目包名(appId)", 999);

//单选本地图片(是否需要裁剪)
pictureSelector.selectPhoto(boolean isCrop);
//多选本地图片(最大可选取数量)
pictureSelector.selectPhotos(int maxNum);
//拍照选择(是否需要裁剪)
pictureSelector.takePhoto(boolean isCrop);

@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    //获取最后的图片(以图片绝对路径的集合的形式返回)
    if (pictureSelector!=null){
        ArrayList<String> images = pictureSelector.getImages(requestCode, data);
        Log.i("imgSelector", images.toString());
    }
}

@Override
protected void onDestroy() {
    super.onDestroy();
    //页面关闭的时候,删除本地缓存目录(可选)
    if (pictureSelector!=null){
        pictureSelector.onDestroy();
    }
}
```