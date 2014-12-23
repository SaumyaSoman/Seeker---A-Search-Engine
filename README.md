Seeker---A-Search-Engine
========================

Seeker is an implementation of a Seach Engine. Web pages of 40 (sports and educational) domains are crawled using Apache Nutch. Inverted index is built from the crawled data using Apache Hadoop. The crawled data and inverted index is saved in nosql MongoDB database for faster response and scalability. The web application communicates with the database using REST webservice deployed in Apache Tomcat server. The web pages are ranked by the the algorithms TF-IDF and Link Analysis.
