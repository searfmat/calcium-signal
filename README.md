# Calcium Signal
***Calcium Signal*** is a plugin for ImageJ that allows you to detect and analyze calcium cells.

## Install

The target platform for this project is Windows 10. We have made it available for MacOS and Linux as well, but extra steps may have to be performed to get things working.
1. From the **release** folder on the github, download **CalciumSignal.jar**
2. Inside of the Fiji.app folder, navigate to plugins
3. Move or copy the **CalciumSignal.jar** file into the folder 

<img src="https://github.com/jstith09/CISC498Project-Group17/blob/main/installation.gif" width="750" height="200">


## Startup & Usage
After launching Fiji, navigate to File -> Open, and open image of choice. Then navigate to Plugins -> Calcium Signal -> Run Calcium Signal....

Running Calcium Signal without an image uploaded to Fiji will prevent it from starting.

Allow a few moments for the image registration and edge detection to complete. Then, make corrections as needed in the ROI Manager dialogue.

After running multi-measure, the peak analysis phase will begin. You will find the peak analysis outputs in Fiji.app/plugins/CalciumSignal/pythonscript/cell_data.

<img src="https://github.com/jstith09/CISC498Project-Group17/blob/main/welcome.png" width="350" height="150">

### PoorMan3DReg
<img src="https://github.com/jstith09/calcium/blob/main/poorman.jpg" width="200" height="150">

  - ***Transformation:*** 
    - Choose a different selection to pick how the image will be changed
      - Rigid Body(Default)
        - Default, Translation + Rotation
      - Translation
      - Scaled Rotation
        - Translation + Rotation + Scaling
      - Affine
        - Translation + Rotation + Scaling + Skewing
  - ***Z Slices:*** 600 (Default) select number of slices (images) you would like from video
  - ***Projection Type:***
    - Average Intensity (Default)
      - Uses an average of the values of the voxels
    - Max Intensity
      - Uses the maximum value of the voxels
    - Sum Slices
      - Averages between the individual slices of voxels
 
### Cell Detector
<img src="https://github.com/jstith09/CISC498Project-Group17/blob/main/celldetector.jpg" width="200" height="200">

  - ***Threshold:*** Automatically will set to a number. Adjust so that the red is covering the most green that you would like detected in the image window
  - ***Slice:*** Set to 1 (Default)
  - ***Size Filter:*** Min: 10 Max: 262144 (Default)
    - Min and Max size of circles drawn. Can be left as is.
  - ***Exclude objects on edges:*** Checked (Default)

## Windows

### Image Window
<img src="https://github.com/jstith09/CISC498Project-Group17/blob/main/calciumwindow.jpg" width="200" height="200">

***DISPLAYS WINDOW WITH THE IMAGE YOU OPENED***
  - Detected cells are circled in yellow (ROI)
  - Select the ROI by clicking on them
  - Selected ROI will appear in Roi Manager where you are able to add/delete/rename them


***NEED TO IMPLEMENT/UPDATE PICTURE***

### Custom Roi Manager
<img src="https://github.com/jstith09/CISC498Project-Group17/blob/main/customroimanager.jpg">

  - ***Min/Max Cell Diameter:*** Enter values to change what size cells are circled by ROI
  - ***Update Cells:*** Use to update ROI after selecting RC 1/2/3 STD
  - ***Multi Measure:*** Uses the built in ImageJ Multimeasure command to take two ore more slices and take the Area, Min, and Max of all of them then store the result as one measurement.
  - ***Outlier Analysis:*** Displays an outlier analysis chart
  - ***RC 1/2/3 STD:*** Automatically enters 1/2/3 different standard deviations for min/max diameter values
  - ***Grid Suggestions:*** Gives suggested values for 'Create Grid'
  - ***Create Grid:*** Enter values from 'Grid Suggestions' to create grid on image
  - ***Generate Graphs*** Uses ROI values to generate graphs from the data
  - ***Undo [Cntrl + Shift + E]:*** Undo Add/Delete of ROI

### Results Table
<img src="https://github.com/jstith09/CISC498Project-Group17/blob/main/resultstable.jpg" width="350" height="150">

  - Allows for saving and editing of the results of the slices
  - Contains different measurements and labels depending on the operations used

### ROI Manager
<img src="https://github.com/jstith09/CISC498Project-Group17/blob/main/roimanager.jpg" width="150" height="200">

  - ***Add:*** When a part of the image is selected, this will add the specific pixels selected to the list. If no part of the image is selected, will return an error.
  - ***Update:*** With an item in the list selected, allows the user to change the size of selected pixels or move the selection. Changes will not be saved unless the update button is clicked a second time after changes were made.
  - ***Delete:*** Deletes the selected item from the list, if nothing is selected, will prompt the user if they wish to delete the entire list.
  - ***Rename:*** Prompts the user to rename the selected item in the list.
  - ***Measure:*** Upon clicking, brings up a new "Results" window, stating the number, mean, minimum, and maximum of the original selection on the image.
<img src="https://github.com/jstith09/CISC498Project-Group17/blob/main/bc6c2b810fab817679cd3362378b3d71.png" width="150" height="150">

  - ***Deselect:*** Deselects the current selection in the group, clicking it while nothing is selected will do nothing.
  - ***Properties:*** Opens up a new tab of the current selection, giving ways to edit the selection
    - ***Name/Range*** Shows the name and allows the user to change it
    - ***Position*** Gives the position of the current selection or group
    - ***Group*** Changes what group the selection is in
    - ***Stroke/Fill color*** Changes the color of the lines/fill of the selection block on the image.
    - ***Width*** Changes the stroke width of the selection box.
    - ***List coordinates*** Lists the coordinates
  - ***Flatten:*** Creates a new RGB image that has the overlay rendered as pixel data.
  - ***More>>:*** ETC.

## Credits

***Plugins:***

GroupedZProjector written by ***Charlie Holly***

PoorMan3DReg written by ***Michael Liebling [link](http://www.its.caltech.edu/~liebling/)***

TurboReg written by ***Philippe Thevenaz [link](http://bigwww.epfl.ch/)***

***Contributers:***

***Matt Searfass, CJ Spagnolia, Nick Costley, Luke Halko, Jaden Stith, Emily Oldham, Sean McMann, Gia Bugieda, Akshat Katoch, Emma Adelmann, Sohan Gadiraju, Jonathan Zhang***


