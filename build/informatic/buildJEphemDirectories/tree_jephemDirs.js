foldersTree = gFld("JEphem", "","")
  aux1 = insFld(foldersTree, gFld("data", "", ""))
	  aux2 = insFld(aux1, gFld("astro", "", ""))
	  aux2 = insFld(aux1, gFld("lang", "", ""))
	  aux2 = insFld(aux1, gFld("prefs", "", ""))
  aux1 = insFld(foldersTree, gFld("java", "", ""))
	  aux2 = insFld(aux1, gFld("bin", "", ""))
	  aux2 = insFld(aux1, gFld("lib", "", ""))
	  aux2 = insFld(aux1, gFld("src", "", ""))
    //insDoc(aux1, gLnk(0, "JEphemApplet.java", "src/jephem/gui/JEphemApplet.java.htm"))
  aux1 = insFld(foldersTree, gFld("web", "", ""))
    insDoc(aux1, gLnk(0, "index.htm", "src/jephem/gui/JEphemApplet.java.htm"))
  	aux2 = insFld(aux1, gFld("dir1", "", ""))
    	aux3 = insFld(aux2, gFld("images", "", ""))
  	aux2 = insFld(aux1, gFld("dir2", "", ""))
  	aux2 = insFld(aux1, gFld("meta", "", ""))
    	aux3 = insFld(aux2, gFld("css", "", ""))
    	aux3 = insFld(aux2, gFld("images", "", ""))
    	aux3 = insFld(aux2, gFld("javascript", "", ""))
  	aux2 = insFld(aux1, gFld("zip", "", ""))

