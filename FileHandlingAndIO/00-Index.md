# Java File Handling and I/O Track

This folder contains runnable Q&A-style demos for Java file handling and I/O, from text files to binary streams and NIO basics.

## Suggested order
1. `TextFileIOQnA.java`
2. `BinaryFileIOQnA.java`
3. `PathFilesAndDirectoryOpsQnA.java`
4. `NIOChannelsAndBuffersQnA.java`
5. `SerializationQnA.java`

## What this folder covers well
- text file reading and writing
- byte streams and primitive binary I/O
- `Path` and `Files` APIs
- directory creation, listing, copying, moving, deleting
- `FileChannel` and `ByteBuffer` basics
- serialization, transient fields, and common traps

## How to use these notes
- Run the code and inspect the temp files/paths mentally as the demos progress.
- Learn when to use character streams vs byte streams.
- Learn modern `Path`/`Files` APIs before relying on older `File`-style thinking.