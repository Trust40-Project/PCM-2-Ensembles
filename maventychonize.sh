# This script can be used to make an Eclipse plug-in project ready to be build with maven tycho.
# It creates and initialize a feature, and update site as well as all pom.xml files.
#
# usage: 
# ------
# Copy all files in this folder (templates and this bash script) into the main folder 
# of the repository that you want to maventychonize. To avoid overriding your README.d
# you can, for example, call "cp -i * yourRepo".
# Change into the repository main folder and call this script as follows:
# ./maventychonize.sh groupIDPrefix plugInName githubOrganization where 
#      groupdIDPrefix is usually of the form 'tld.domain.subdomain.whatever',
#      plugInName is usually of the form 'someShortName', and
#      githubOrganization is often something like 'shortorg' or a github user name.
#
# prequisites:
# ------------
# You need to have
#    a plug-in project named '<groupIDPrefix>.<plugInName>'
#       stored in a folder named 'bundles', 
#    a plug-in project with JUnit tests named '<groupIDPrefix>.<plugInName>.tests'
#       stored in a folder named 'tests'.
# 
# result:
# -------
# By running this script you will obtain
#    a feature project named '<groupIDPrefix>.<plugInName>.feature'
#        stored in a folder named 'features', 
#    pom.xml files in the 'bundles' and 'features' folder,
#        which are required for a POM-less build of the plug-ins and features in subfolders of these folders,
#    an aggregator pom.xml,
#        where you list all plug-ins and features for the build,
#    a parent pom.xml, 
#        for the main build configuration,
#    an update site project named '<groupIDPrefix>.<plugInName>.updatesite'
#        stored in the same folder 'releng',
#    an aggregated update site project named '<groupIDPrefix>.<plugInName>.updatesite.aggregated'
#        stored in the same folder 'releng', and
#    a travis configuration
#	 stored as '.travis.yml'.
#
# follow-up:
# ----------
# After running the script you still have to 
#    change the dependencies to features of other update sites in the
#       releng/...updatesite.aggregated/updatesite.aggr file,
#          (You do not need to repeat a feature x if you state that you depend on a feature y that depends on x.) 
#    add these update sites to the
#       releng/...parent/pom.xml file, and
#          (Note that you explicitly have to state that the build depends on an update site A 
#           even if you already stated that the build depends on an update site B, which depends on A.)
#    add these features to the
#       features/.../feature.xml file.
#
# If you have more than one Eclipse plug-in to be included in the build, then you have to
#    add these plug-ins to the 
#       releng/...aggregator/pom.xml file and to the
#	features/.../feature.xml file. 
#
# Furthermore, you may want to 
#    add a description, copyright, and license text to the 
#        features/.../feature.xml file and 
#    add a category description for the update site and change the label for it (as it will be displayed to users) in the
#        releng/...updatesite/category.xml file.
#
# At all these locations in the files we have already put TODO comments for you.
# You can find them by running "grep 'TODO' */*/*.aggr */*/*.xml"
#
# Finally, you have to create an OAuth access token in your github settings and 
# add its value for the evironment variable 'GITHUB_DEPLOY_TOKEN' (and do not display it in build log ;-)
#
#########################################################################################
# input:
GROUPIDPREFIX=$1;
PLUGINNAME=$2;
GROUPID=$GROUPIDPREFIX.$PLUGINNAME;
GITHUBORGANIZATION=$3;
# bundles/
BUNDLESFOLDER=bundles;
BUNDLESPOM=$BUNDLESFOLDER/pom.xml;
# features/
FEATURESFOLDER=features;
FEATURESPOM=$FEATURESFOLDER/pom.xml;
FEATUREFOLDER=features/$GROUPID.feature;
FEATUREPROJECT=$FEATUREFOLDER/.project;
FEATUREBUILDPROPS=$FEATUREFOLDER/build.properties;
FEATUREXML=$FEATUREFOLDER/feature.xml;
# .
MAINPOM=pom.xml;
# releng/
AGGREGATORFOLDER=releng/$GROUPID.aggregator;
AGGREGATORPOM=$AGGREGATORFOLDER/pom.xml;
PARENTFOLDER=releng/$GROUPID.parent;
PARENTPOM=$PARENTFOLDER/pom.xml;
UPDATESITEFOLDER=releng/$GROUPID.updatesite;
UPDATESITEPROJECT=$UPDATESITEFOLDER/.project;
UPDATESITECATEGORY=$UPDATESITEFOLDER/category.xml;
UPDATESITEPOM=$UPDATESITEFOLDER/pom.xml
UPDATESITEAGGRFOLDER=$UPDATESITEFOLDER.aggregated;
UPDATESITEAGGRPROJECT=$UPDATESITEAGGRFOLDER/.project;
UPDATESITEAGGR=$UPDATESITEAGGRFOLDER/updatesite.aggr;
UPDATESITEAGGRPOM=$UPDATESITEAGGRFOLDER/pom.xml;
# .travis.yml
TRAVISYML=.travis.yml;
#########################################################################################
# bundles/
if [ ! -d "$BUNDLESFOLDER" ]; then
  mkdir -v $BUNDLESFOLDER;
fi
mv -v bundle_TEMPLATE.pom $BUNDLESPOM;
sed -i 's/thePlugInName/'$PLUGINNAME'/g' $BUNDLESPOM;
sed -i 's/theGroupID/'$GROUPID'/g' $BUNDLESPOM;
# features/
mkdir -v -p $FEATUREFOLDER;
mv -v feature_TEMPLATE.project $FEATUREPROJECT;
sed -i 's/theGroupID/'$GROUPID'/g' $FEATUREPROJECT;
mv -v feature_build_TEMPLATE.properties $FEATUREBUILDPROPS;
mv -v feature_TEMPLATE.xml $FEATUREXML;
sed -i 's/thePlugInName/'$PLUGINNAME'/g' $FEATUREXML;
sed -i 's/theGroupID/'$GROUPID'/g' $FEATUREXML;
mv -v features_TEMPLATE.pom $FEATURESPOM;
sed -i 's/thePlugInName/'$PLUGINNAME'/g' $FEATURESPOM;
sed -i 's/theGroupID/'$GROUPID'/g' $FEATURESPOM;
# .mvn/
mkdir -v .mvn;
mv -v extensions.xml .mvn/;
# .
mv -v main_TEMPLATE.pom $MAINPOM;
sed -i 's/theGroupID/'$GROUPID'/g' $MAINPOM;
# releng/...aggregator
mkdir -v -p $AGGREGATORFOLDER;
mv -v aggregator_TEMPLATE.pom $AGGREGATORPOM;
sed -i 's/theGroupID/'$GROUPID'/g' $AGGREGATORPOM;
# releng/...parent
mkdir -v $PARENTFOLDER;
mv -v parent_TEMPLATE.pom $PARENTPOM;
sed -i 's/theGroupID/'$GROUPID'/g' $PARENTPOM;
# releng/...updatesite
mkdir -v $UPDATESITEFOLDER;
mv -v updatesite_TEMPLATE.project $UPDATESITEPROJECT;
sed -i 's/theGroupID/'$GROUPID'/g' $UPDATESITEPROJECT;
mv -v category_TEMPLATE.xml $UPDATESITECATEGORY;
sed -i 's/thePlugInName/'$PLUGINNAME'/g' $UPDATESITECATEGORY;
sed -i 's/theGroupID/'$GROUPID'/g' $UPDATESITECATEGORY;
mv -v updatesite_TEMPLATE.pom $UPDATESITEPOM;
sed -i 's/thePlugInName/'$PLUGINNAME'/g' $UPDATESITEPOM;
sed -i 's/theGroupID/'$GROUPID'/g' $UPDATESITEPOM;
# releng/...updatesite
mkdir -v $UPDATESITEAGGRFOLDER;
mv -v updatesiteaggregated_TEMPLATE.project $UPDATESITEAGGRPROJECT;
sed -i 's/theGroupID/'$GROUPID'/g' $UPDATESITEAGGRPROJECT;
mv -v updatesiteaggregated_TEMPLATE.aggr $UPDATESITEAGGR;
sed -i 's/thePlugInName/'$PLUGINNAME'/g' $UPDATESITEAGGR;
sed -i 's/theGroupID/'$GROUPID'/g' $UPDATESITEAGGR;
mv -v updatesiteaggregated_TEMPLATE.pom $UPDATESITEAGGRPOM;
sed -i 's/thePlugInName/'$PLUGINNAME'/g' $UPDATESITEAGGRPOM;
sed -i 's/theGroupID/'$GROUPID'/g' $UPDATESITEAGGRPOM;
# .travis.yml
mv -v travis_TEMPLATE.yml $TRAVISYML;
sed -i 's/thePlugInName/'$PLUGINNAME'/g' $TRAVISYML;
sed -i 's/theGroupID/'$GROUPID'/g' $TRAVISYML;
sed -i 's/theGithubOrganization/'$GITHUBORGANIZATION'/g' $TRAVISYML;
#########################################################################################
echo "maventychonization done";