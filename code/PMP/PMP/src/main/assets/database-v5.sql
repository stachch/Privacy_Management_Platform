CREATE TABLE IF NOT EXISTS ResourceGroup (
	Package TEXT NOT NULL,
	PRIMARY KEY(Package)
);


CREATE TABLE IF NOT EXISTS PrivacySetting (
	ResourceGroupPackage TEXT NOT NULL,
	Identifier TEXT NOT NULL,
	Requestable INT NOT NULL,
	PRIMARY KEY(ResourceGroupPackage,
	            Identifier)
);


CREATE TABLE IF NOT EXISTS App (
	Package TEXT NOT NULL,
	PRIMARY KEY(Package)
);


CREATE TABLE IF NOT EXISTS ServiceFeature (
	AppPackage TEXT NOT NULL,
	Identifier TEXT NOT NULL,
	PRIMARY KEY(AppPackage,
	            Identifier)
);


CREATE TABLE IF NOT EXISTS Preset (
	Creator TEXT NOT NULL,
	Identifier TEXT NOT NULL,
	Name TEXT NOT NULL,
	Description TEXT NOT NULL,
	Deleted TEXT NOT NULL,
	PRIMARY KEY(Creator,
	            Identifier)
);


CREATE TABLE IF NOT EXISTS ServiceFeature_RequiredPrivacySettingValue (
	PrivacySettingResourceGroupPackage TEXT NOT NULL,
	PrivacySettingIdentifier TEXT NOT NULL,
	ServiceFeatureAppPackage TEXT NOT NULL,
	ServiceFeatureIdentifier TEXT NOT NULL,	
	RequiredValue TEXT NOT NULL,
	PRIMARY KEY(PrivacySettingResourceGroupPackage,
	            PrivacySettingIdentifier,
				ServiceFeatureAppPackage,
				ServiceFeatureIdentifier)
);


CREATE TABLE IF NOT EXISTS Preset_GrantedPrivacySettingValue (
	PrivacySettingResourceGroupPackage TEXT NOT NULL,
	PrivacySettingIdentifier TEXT NOT NULL,
	PresetCreator TEXT NOT NULL,
	PresetIdentifier TEXT NOT NULL,	
	GrantedValue TEXT NOT NULL,
	PRIMARY KEY(PrivacySettingResourceGroupPackage,
	            PrivacySettingIdentifier,
				PresetCreator,
				PresetIdentifier)
);


CREATE TABLE IF NOT EXISTS Preset_AssignedApp (
	PresetCreator TEXT NOT NULL,
	PresetIdentifier TEXT NOT NULL,	
	AppPackage TEXT NOT NULL,
	PRIMARY KEY(PresetCreator,
				PresetIdentifier,
	            AppPackage)
);


CREATE TABLE IF NOT EXISTS Context_AnnotatedPrivacySettingValue (
	PrivacySettingResourceGroupPackage TEXT NOT NULL,
	PrivacySettingIdentifier TEXT NOT NULL,
	PresetCreator TEXT NOT NULL,
	PresetIdentifier TEXT NOT NULL,
	PresetPrivacySettingAnnotationID INT NOT NULL,
	ContextType TEXT NOT NULL,
	ContextCondition TEXT NOT NULL,
	OverrideGrantedValue TEXT NOT NULL,
	PRIMARY KEY(PrivacySettingResourceGroupPackage,
	            PrivacySettingIdentifier,
				PresetCreator,
				PresetIdentifier,
				PresetPrivacySettingAnnotationID)
);