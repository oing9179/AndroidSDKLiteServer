## 1. Where do I download xml files?
All files download from here: [https://dl.google.com/android/repository/](https://dl.google.com/android/repository/).
First of all, we need tow of xml files to start:
[https://dl.google.com/android/repository/addons_list-3.xml](https://dl.google.com/android/repository/addons_list-3.xml)
[https://dl.google.com/android/repository/repository2-1.xml](https://dl.google.com/android/repository/repository2-1.xml)

For repository2-1.xml:
```xml
<remotePackage>
	<blabla>...</blabla>
	<archives>
		<archive>
			<complete>
				<url>somefile.zip</url>
				<url>or/url/like/this.zip</url>
				<url>https://or.url.like/this.zip</url>
			</complete>
		</archive>
	</archives>
</remotePackage>
```
Extract text from `<url>somedir/likethis.zip</url>` we got: `somedir/likethis.zip`. put it after url and we got:
```
https://dl.google.com/android/repository/somedir/likethis.zip
```
After that, this url is now ready to access to download.
**But Here is an exception:**
```xml
<url>https://or.url.like/this.zip</url>
```
For this one, just download it!

For addons_list-3.xml:
```xml
<site>
	<displayName>blablabla</displayName>
	<url>sys-img/google_apis/sys-img2-1.xml</url>
</site>
```
As you see, almost same structure as repository2-1.xml, so rules for repository2-1.xml also applies to this file.

## 2. Where I can find XSD(Xml Schema Definition) for "repository2-1.xml" and "addons_list-3.xml"?
All xml definition can be found from inside of following jar files:
First, you need a lastest version of Android Studio, then goto directory:
`<AndroidStudio>/plugins/android/lib/`
Inside of those jar files:
```
repository.jar
sdklib.jar
```
