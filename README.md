![OpenNARS Logo](https://github.com/opennars/opennars/blob/bf53ceef9f2399de70dc63e5507e42d639144c96/doc/opennars_logo2.png)
**Open-NARS** is the open-source version of [NARS](https://sites.google.com/site/narswang/home), a general-purpose AI system, designed in the framework of a reasoning system.  This project is an evolution of the [v1.5 system](http://code.google.com/p/open-nars/).  The [mailing list](https://groups.google.com/forum/?fromgroups#!forum/open-nars) discusses both its theory and implementation.

[![Build Status](https://travis-ci.org/opennars/opennars.svg?branch=master)](https://travis-ci.org/opennars/opennars)
[![codecov](https://codecov.io/gh/opennars/opennars/branch/master/graph/badge.svg)](https://codecov.io/gh/opennars/opennars)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/fce375943907463fa53dc5bebcefebbd)](https://www.codacy.com/app/freemo/opennars?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=opennars/opennars&amp;utm_campaign=Badge_Grade)

Overview
--------

This project provides runnable demo examples the Non-Axiomatic Reasoning System implementation, OpenNARS (see https://github.com/opennars/opennars).

How to build OpenNARS
---------------------

Install git https://git-scm.com/downloads

Install OpenJDK 11 https://jdk.java.net/11/

Install community edition IntelliJ https://www.jetbrains.com/idea/download/

Checkout https://github.com/opennars/opennars.git

Checkout https://github.com/opennars/opennars-lab.git

Checkout https://github.com/opennars/opennars-applications.git

You can either checkout within Intellij or use the Github desktop (availble from the github clone button in the repo)

Build opennars
--------------
If this is a fresh install you will be prompted to enter the jdk path (where you installed it above)
You may be prompted to update maven dependencies - do this if prompted

Build opennars-lab
------------------
Select org.opennars.lab.launcher.Launcher as the main entry point

Build opennars-applications
---------------------------
Select org.opennars.applications.Launcher


The launchers are the easiest way to run the various apps

opennars-lab 
------------
Main GUI	Main user interface for NARS
Test Chamber	Simulation environment for testing behaviours
Micro world	Behaviour learning by simple insect like creature
NAR Pong	The classic pong game

Language Lab	For experimenting with parts of speech (POS) and grammar learning
Perception Test Pattern matching experiment

Prediction Test Predicts a waveform - Can be run directly from Intellij 
		(Current issue with running with launcher)
Vision		Vision expeirment - Can be run direcly from Intellij 
		(Current issue with running with launcher)

opennars-applications
---------------------
Main GUI - A simple MIT license GUI - 
Crossing - A snart city traffic intersection suimulation
Identity mapping - An experimental setup for testing aspects of Relations Frame Theory (RVT)

opennars
--------
Core - Launchers run this directly

Contents
--------
Demo example that show several capabilities of OpenNARS.

Run Requirements
----------------
 * Java 8+ (OpenJDK 10 recommended)


Development Requirements
------------------------
 * Maven

Links
-----
 * [Website](http://opennars.github.io/opennars/)
 * [All downloads](https://drive.google.com/drive/folders/0B8Z4Yige07tBUk5LSUtxSGY0eVk?usp=sharing)
 * [An (outdated) HTML user manual](http://www.cis.temple.edu/~pwang/Implementation/NARS/NARS-GUI-Guide.html)
 * [The Project homepage](https://code.google.com/p/open-nars/)
 * [google groups - Discussion Group](https://groups.google.com/forum/?fromgroups#!forum/open-nars)
 * [IRC](http://webchat.freenode.net?channels=nars)
 * [Try online](http://91.203.212.130/NARS)

Credits:
-------
We also thank the involved other projects, for example
http://www.marioai.org/LevelGeneration/source-code
https://processing.org/
Hyperassociative Map code adapted from dANN:
http://wiki.syncleus.com/index.php/dANN
