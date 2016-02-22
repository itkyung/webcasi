package com.kbsmc.webcasi.admin;

import com.kbsmc.webcasi.entity.HelpContents;

public interface IHelpContentsDAO {
	void createHelpContents(HelpContents help);
	void saveHelpContents(HelpContents help);
	HelpContents loadHelpContents(String id);
}
