
    _____                       ___    ___    ___   
   (  _  )                     (  _`\ (  _`\ (  _`\ 
   | ( ) | _ _      __    ___  | ( (_)| | ) || ( (_)
   | | | |( '_`\  /'__`\/' _ `\| |___ | | | )| |  _ 
   | (_) || (_) )(  ___/| ( ) || (_, )| |_) || (_( )
   (_____)| ,__/'`\____)(_) (_)(____/'(____/'(____/'
          | |                                       
          (_)                                       

#########################################################################


This is the Readme for the OpenGDC software tool, suitable for
the download and conversion of all the public available genomic 
and clinical data of the Genomic Data Commons portal to the BED 
format.

It introduces the OpenGDC package and explains how to run and 
use the program.


=======
Package
=======

OpenGDC_readme.txt -- this file
OpenGDC_User_Guide.pdf -- the user guide of OpenGDC
OpenGDC.jar -- the Java application
lib -- contains the Java application dependencies
appdata -- contains some files needed for the proper software execution,
	   in particular four different datasets from external public
	   databases useful for the retrieval operation of genomic
	   coordinates


=========
Execution
=========

The provided OpenGDC.jar is a runnable Java application.
The program needs Java 1.8 that is freely available for download from
Oracle website http://www.oracle.com/technetwork/java/javase/downloads/
To start running the application, the folders named 'appdata' and
'lib' have to be located in the same directory of OpenGDC.jar.
OpenGDC can be run by double clicking it, or through the command line
instruction "java -jar OpenGDC.jar" if the Java installation path
is in the PATH and CLASSPATH environment variables of the operating
system in use.


==========
How To Use
==========

The application is composed of two sections responsible for the download 
and the conversion of all the public available genomic and clinical data
from the Genomic Data Commons repository.

------------------
OpenGDC Downloader
------------------

The "Downloader" section of the application window allows the download
of all public available genomic and clinical data from the Genomic
Data Commons.
For downloading the requested data, the user has to select the desired
program (e.g., TCGA), disease, and data type by scrolling the drop-down
menu.
After the selection of the folder where the requested data set will be 
downloader, the user can start the data retrieval process by clicking
the "Download" button.

-----------------
OpenGDC Converter
-----------------

The "Converter" section of the application window allows the conversion
of genomic and clinical data from the original format provided by the 
Genomic Data Commons to the free BED standard format.
The information needed for this operation are the program, disease, and 
the data type of the data that will be converted.
Before clicking the "Convert" button, it is necessary to specify the
folders containing clinical and genomic data of the considered disease,
the experiment type, and the desired output format (BED, CSV, GTF, JSON,
or XML).
After the selection of the folder in which the specified data set will 
be converted, the user can start the data conversion process by clicking
the "Convert" button.


=========
Licensing
=========

Copyright [2018] [IASI-CNR]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.


========
Contacts
========
Fabio Cumbo: fabio.cumbo@iasi.cnr.it
Eleonora Cappelli: eleonora.cappelli@uniroma3.it

