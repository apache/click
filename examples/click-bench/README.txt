How to run benchmark
====================

To run the benchmark, copy one of the war files under the 'archive' folder
to your application server. The archive folder contains benchmarks for different
Click versions. All jars are already included in the war file.

Start your server and navigate to:

http://<host>:<port>/click-bench

To start the benchmark download a copy of Apache JMeter from http://jakarta.apache.org/jmeter/

Open the script jmeter/benchmark.jmx with Apache JMeter and select the specific
benchmark to run.

JDK version
===========

This benchmark will only run on JDK5 and higher.

Build from source
=================

You can also build the project from source by running the following ant command:

ant build

This will compile and build a new war file in the 'dist' folder.

To remove all built artifacts use the ant command:

ant clean