# Finvoice XML to Procountor CSV

### How to use
Once you get the program up and running access it in `localhost:8080`
1. Click 'Upload' button and browse for the XML file
2. You should see a quick notification that conversion was successful
3. Click 'Download' button to get the CSV file

## Getting started
Open a terminal and type:
```bash
git clone https://github.com/valtterikodisto/xmltocsv.git
```

### Prerequisites
First you will need Java and Maven. If you dont have them installed, open a terminal and type:
```bash
sudo apt-get update
```
For Java
```bash
sudo apt-get install default-jdk
```
For Maven
```bash
sudo apt install maven
```

### Get it up and running
```bash
cd xmltocsv/
mvn spring-boot:run
```

### Docker option
You can also use Docker to run the project but building the docker image will take some time 
since we need to download and install a lot of files.
```bash
cd xmltocsv/
docker build -t xmltocsv .
docker run -d -p 8080:8080 --name converter xmltocsv
```
When you want to remove the docker image, do the following:
```bash
docker rm -f converter
docker rmi xmltocsv
```
If you did not have ubuntu:16.04 image:
```bash
docker rmi ubuntu:16.04
```

### About CSV template files
#### Location
[Template files](https://github.com/valtterikodisto/xmltocsv/tree/master/src/main/resources/templates) can be found at xmltocsv/src/main/resources/templates

#### Syntax
Each line represents a value in the CSV row in order
```
TagName => renders tag's text value
TagName:Attribute => renders tag's attribute
[value] => renders value as it is (hardcoded values)
EMPTY => renders an empty value
TagThatIsNotInTheXMLFile => renders an empty value
```

#### More
I made the template file so that it is easy to
change order of the CSV values and add them 
(without need to touch the code in any way). 

You could easily add more fields in the template
files if you need to and the template file can include
fields that are not present in the xml file. They just
render an empty string in the csv file as they should!
