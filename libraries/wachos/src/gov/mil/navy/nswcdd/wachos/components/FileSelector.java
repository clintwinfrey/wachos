/**
 * The WACHOS software library is developed by the U.S. Department of Defense
 * (DoD).  It is made available to the public under the terms of the Apache
 * License, Version 2.0.
 *
 * Copyright (c) 2025, Naval Surface Warfare Center, Dahlgren Division.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Legal Notice: This software is subject to U.S. government licensing and
 * export control regulations. Unauthorized use, duplication, or distribution is
 * prohibited. All rights to this software are held by the U.S. Department of
 * Defense or its contractors.
 *
 * Patent Notice: This software may be subject to one or more patent
 * applications. Users of the software should ensure they comply with any
 * licensing or usage terms associated with the patent(s). For more
 * information, please refer to the patent application (Navy Case 109347,
 * 18/125,944).
 *
 * @author Clinton Winfrey
 * @version 1.0
 * @since 2025
 */
package gov.mil.navy.nswcdd.wachos.components;

import gov.mil.navy.nswcdd.wachos.components.layout.Dialog;
import gov.mil.navy.nswcdd.wachos.components.layout.HBox;
import gov.mil.navy.nswcdd.wachos.components.layout.Layout;
import gov.mil.navy.nswcdd.wachos.components.layout.VBox;
import gov.mil.navy.nswcdd.wachos.components.text.Button;
import gov.mil.navy.nswcdd.wachos.components.text.ImageButton;
import gov.mil.navy.nswcdd.wachos.components.text.Label;
import gov.mil.navy.nswcdd.wachos.tools.WFileServlet;
import gov.mil.navy.nswcdd.wachos.tools.WTools;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * FileSelector allows the user to select a file that is located on the server
 * side
 */
public class FileSelector extends VBox {

    /**
     * selecting file of type directory
     */
    public static final String DIRECTORY = "DIRECTORY";
    /**
     * selecting any file (not a directory, not filtered at all)
     */
    public static final String FILE = "FILE";
    /**
     * selecting file that is an image
     */
    public static final String[] IMAGES = new String[]{"jpg", "jpeg", "gif", "png"};
    /**
     * the popup that allows you to select a file
     */
    private final Dialog dialog;
    /**
     * filters out files that the user wouldn't care about
     */
    private FilenameFilter filter;
    /**
     * the highest folder to which this selector allows access
     */
    private final String topDirectory;
    /**
     * folders that can be accessed for browsing/selecting
     */
    private final List<String> accessibleFolders;
    /**
     * the directory the user is currently browsing in
     */
    private File currentDirectory;
    /**
     * label that says the directory the user is currently browsing in
     */
    private Label currentDirectoryLabel = new Label("");
    /**
     * listens for when a file is selected
     */
    private final FileSelectListener selectListener;
    /**
     * lists the files that can be selected
     */
    private final ListBox fileSelector = new ListBox();
    /**
     * the file that is currently selected by the user
     */
    private File selection;
    /**
     * flag indicating if a file is currently selected
     */
    private boolean fileSelected = false;
    /**
     * used to select the folder that the user last browsed in
     */
    private final String cookieId;
    /**
     * the files in the folder that is currently being browsed, but with a
     * folder or file icon to the left
     */
    private List<String> filesInFolder = new ArrayList<>();

    /**
     * Creates a FileSelector
     *
     * @param parent the thing that is opening this FileSelector, needed as a
     * reference
     * @param selectListener thing that happens when file is selected
     * @param fileTypes types of files that can be selected; if you want to
     * select .txt files, you would put "txt" here. If you want to select a
     * directory, use FileSelector.DIRECTORY. If you want to select any file and
     * not filter according to type, put FileSelector.FILE
     */
    public static void create(Layout parent, FileSelectListener selectListener, String... fileTypes) {
        create(parent, selectListener, null, null, fileTypes);
    }

    /**
     * Creates a FileSelector
     *
     * @param parent the thing that is opening this FileSelector, needed as a
     * reference
     * @param selectListener thing that happens when file is selected
     * @param topDirectory do not view above this directory
     * @param fileTypes types of files that can be selected; if you want to
     * select .txt files, you would put "txt" here. If you want to select a
     * directory, use FileSelector.DIRECTORY. If you want to select any file and
     * not filter according to type, put FileSelector.FILE
     */
    public static void create(Layout parent, FileSelectListener selectListener, File topDirectory, String... fileTypes) {
        create(parent, selectListener, topDirectory, null, fileTypes);
    }

    /**
     * Creates a FileSelector
     *
     * @param parent the thing that is opening this FileSelector, needed as a
     * reference
     * @param selectListener thing that happens when file is selected
     * @param topDirectory do not view above this directory
     * @param startingDirectory the directory in which to start browsing
     * @param fileTypes types of files that can be selected; if you want to
     * select txt files, you would put ".txt" here. If you want to select a
     * directory, use FileSelector.DIRECTORY. If you want to select any file and
     * not filter according to type, put FileSelector.FILE
     */
    public static void create(Layout parent, FileSelectListener selectListener, File topDirectory, File startingDirectory, String... fileTypes) {
        Dialog dialog = parent.createDialog("Select File");
        FileSelector selector = new FileSelector(dialog, selectListener, topDirectory, startingDirectory, fileTypes);
        dialog.getContent().add(selector);
        dialog.closeListeners.add(action -> {
            dialog.dispose();
            selector.fireSelection();
        });
        dialog.open();
    }

    /**
     * Creates a FileSelector
     *
     * @param dialog the popup that will be used to select a file
     * @param selectListener thing that happens when file is selected
     * @param topDir do not view above this directory
     * @param startingDir the directory in which to start browsing
     * @param fileTypes types of files that can be selected; if you want to
     * select txt files, you would put ".txt" here. If you want to select a
     * directory, use FileSelector.DIRECTORY. If you want to select any file and
     * not filter according to type, put FileSelector.FILE
     */
    private FileSelector(Dialog dialog, FileSelectListener selectListener, File topDir, File startingDir, String... fileTypes) {
        this.dialog = dialog;
        this.selectListener = selectListener;
        if (topDir == null) {
            this.topDirectory = WFileServlet.getBaseAccessibleFolder();
            this.accessibleFolders = WFileServlet.getAccessibleFolders();
        } else if (WTools.isDesktopMode() || WFileServlet.canAccess(topDir)) {
            this.topDirectory = WFileServlet.getPath(topDir);
            this.accessibleFolders = Arrays.asList(topDirectory);
        } else {
            this.topDirectory = null;
            this.accessibleFolders = null;
            this.cookieId = null;
            return;
        }

        fileSelector.setWidth("300px").setHeight("400px");

        String types = "";
        for (String fileType : fileTypes) {
            types += fileType.replace(".", "_").replace("\\s+", "");
        }
        cookieId = "tfFileSelectDir" + types;

        if (accessibleFolders.isEmpty()) {
            return; //can't select file, because accessible directories haven't been set
        }

        //set up the file filtering
        if (fileTypes.length == 0 || fileTypes[0].equals(FILE)) {
            filter = (folder, name) -> true;
        } else {
            filter = (folder, name) -> {
                for (String fileType : fileTypes) {
                    if (name.toLowerCase().endsWith(fileType.toLowerCase())) {
                        return true;
                    }
                }
                return false;
            };
        }
        FileSelector.this.add(new HBox(new ImageButton("fa-arrow-up", action -> {
            String subdir = currentDirectory.toString().replace(topDirectory, "");
            if (!subdir.equals("")) {
                updateDirectory(currentDirectory.getParentFile());
            }
            //currentDirectory = directory;
        }), currentDirectoryLabel), fileSelector, new HBox(
                new Button("Cancel", action -> {
                    dialog.dispose();
                }), new Button("Open", action -> {
                    if (selection == null) {
                        //don't do anything, nothing is selected
                    } else if (selection.isDirectory() && (fileTypes.length == 0 || !fileTypes[0].equals(DIRECTORY))) {
                        updateDirectory(selection); //this is a directory, and we're not trying to select a directory
                    } else {
                        fileSelected = true;
                        dialog.dispose();
                    }
                })
        ));
        String lastDir = startingDir == null ? WTools.getCookie(cookieId) : WFileServlet.getPath(startingDir);
        updateDirectory(lastDir.equals("") || !lastDir.startsWith(topDirectory) ? new File(topDirectory) : new File(lastDir));
    }

    /**
     * Calls 'fileSelected' on the selectListener
     */
    private void fireSelection() {
        if (fileSelected && selection != null) {
            selectListener.fileSelected(selection);
        }
    }

    /**
     * Updates the currently selected directory to this
     *
     * @param directory the currently selected directory
     */
    private void updateDirectory(File directory) {
        String subdir = directory.toString().replace(topDirectory, "");
        currentDirectory = directory;
        currentDirectoryLabel.setText(subdir.equals("") ? "" : subdir, true, true);
        WTools.setCookie(cookieId, directory.toString());

        boolean currentDirectoryValid = false;
        for (String accessibleFolder : accessibleFolders) {
            if (directory.toString().startsWith(accessibleFolder)) {
                currentDirectoryValid = true;
                break;
            }
        }

        filesInFolder.clear();
        if (currentDirectoryValid) {
            //first find all of the folders
            List<String> folders = new ArrayList<>();
            for (File folder : directory.listFiles((folder, name) -> new File(folder, name).isDirectory())) {
                if (!folder.getName().startsWith(".")) {
                    folders.add("📁 " + folder.getName());
                }
            }
            Collections.sort(folders);

            //next find all of the files that match the filter
            List<String> files = new ArrayList<>();
            for (File file : directory.listFiles(filter)) {
                if (!file.getName().startsWith(".")) {
                    files.add("🖺 " + file.getName());
                }
            }
            Collections.sort(files);

            filesInFolder.addAll(folders);
            filesInFolder.addAll(files);
        } else if (!directory.toString().equals(topDirectory)) {
            updateDirectory(new File(topDirectory)); //go to the top directory so we can choose between accessible folders
            return;
        } else {
            for (String accessibleFolder : accessibleFolders) {
                String curDirPath = WFileServlet.getPath(currentDirectory);
                if (accessibleFolder.startsWith(curDirPath)) {
                    filesInFolder.add("📁 " + accessibleFolder.substring(curDirPath.length() + 1));
                }
            }
            Collections.sort(filesInFolder);
        }

        fileSelector.setOptions(filesInFolder);
        fileSelector.selectionsChangedListeners.add(value -> {
            this.selection = new File(currentDirectory, filesInFolder.get(Integer.parseInt(value.substring(1, value.length() - 1).replace(",", " ").split("\\s+")[0])).substring(2).trim());
        });
        fileSelector.doubleClickListeners.add(value -> {
            this.selection = new File(currentDirectory, value.substring(2).replace("�", "").trim());
            if (selection == null || !selection.exists()) {
                //do nothing
            } else if (selection.isDirectory()) {
                updateDirectory(selection);
            } else {
                fileSelected = true;
                dialog.dispose();
            }
        });
    }

    /**
     * Interface that gets notified for when a file is selected
     */
    public static interface FileSelectListener {

        /**
         * Called when a file has been selected
         *
         * @param file the file that has been selected
         */
        public void fileSelected(File file);
    }

}
