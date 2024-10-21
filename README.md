# What's it?
OpenBuilder is an apk file builder directly on your android device without a computer connection or internet connection.

# Thanks
- Maximoff for making ZipAlign and AAPT binaries publicly available for all architectures. ( https://github.com/Maximoff )
- Codyi96 for xml2axml. ( https://github.com/codyi96/xml2axml )

# Instructions for replacing the apk template
- Create a new apk file in any development environment, then copy it, open it as a Zip archive and extract AndroidManifest.xml .
- Make sure that you have JDK installed and it is added to the environment variables of your operating system.
- Use xml2axml.jar from "libs" and enter "java -jar xml2axml-2.0.1.jar d AndroidManifest.xml manifest.xml ".
- Next, open it and replace your package (for example, com.name.app) with SLONOAPP_PACKAGE. Also with the version, version code and application name.
- Delete the old one manifest.xml from assets/ and paste your own, then replace slonoGame.apk with your created apk.
