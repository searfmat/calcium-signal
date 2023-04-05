
input = getDirectory("Choose a data folder to process");
output = getDirectory("Choose a folder to save processed images");
suffix = "lsm";

processFolder(input);

// function to scan folders/subfolders/files to find files with correct suffix
function processFolder(input) {
	list = getFileList(input);
	list = Array.sort(list);
	for (i = 0; i < list.length; i++) {
		if(File.isDirectory(input + File.separator + list[i]))
			processFolder(input + File.separator + list[i]);
		if(endsWith(list[i], suffix))
			processFile(input, output, list[i]);
	}
}

function processFile(input, output, file) {
	// Do the processing here by adding your own code.
	// Leave the print statements until things work, then remove them.
	//run("Brightness/Contrast...");
	open(file);
    run("Enhance Contrast", "saturated=0.35");
    run("Enhance Contrast", "saturated=0.35");
    run("Apply LUT", "stack");
    saveAs("tiff", output + File.separator + file);
    run("Close");
	print("Processing: " + input + File.separator + file);
	print("Saving to: " + output);
}
