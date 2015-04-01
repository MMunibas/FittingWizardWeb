#!/bin/bash

sqlite3 test.sqlite "select topFile from top where id=1" | sed 's/\\n/\n/g'

