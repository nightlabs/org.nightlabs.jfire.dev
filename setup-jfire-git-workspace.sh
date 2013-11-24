echo "$1"
echo "$2"
echo "$3"

COMMAND="$1"
SKIPTESTS="$2"
if [ -n "$X" ]; then
    SKIPTESTS="false"
fi

MVN_GOALS="$3"
if [[ -z "$3" ]]; then
    MVN_GOALS="clean install"
fi

case $COMMAND in
create)
    git clone https://github.com/nightlabs/org.nightlabs.git org.nightlabs
    git clone https://github.com/nightlabs/org.nightlabs.eclipse.git org.nightlabs.eclipse
    git clone https://github.com/nightlabs/org.nightlabs.jfire.min.git org.nightlabs.jfire.min
    git clone https://github.com/nightlabs/org.nightlabs.jfire.min.eclipse.git org.nightlabs.jfire.min.eclipse
    git clone https://github.com/nightlabs/org.nightlabs.jfire.max.git org.nightlabs.jfire.max
    git clone https://github.com/nightlabs/org.nightlabs.jfire.max.eclipse.git org.nightlabs.jfire.max.eclipse
    ;;
update)
    git pull org.nightlabs
    git pull org.nightlabs.eclipse
    git pull org.nightlabs.jfire.min
    git pull org.nightlabs.jfire.min.eclipse
    git pull org.nightlabs.jfire.max
    git pull org.nightlabs.jfire.max.eclipse
    ;;
esac

echo "[INFO] ------------------------------------------------------------------------"
echo "[INFO] mvn $MVN_GOALS -f org.nightlabs/org.nightlabs.parent/pom-aggregator.xml -DskipTests=$SKIPTESTS"
echo "[INFO] ------------------------------------------------------------------------"
mvn $MVN_GOALS -f org.nightlabs/org.nightlabs.parent/pom-aggregator.xml -DskipTests=$SKIPTESTS
if [[ $? != 0 ]] ; then    
    exit $?
fi

echo "[INFO] ------------------------------------------------------------------------"
echo "[INFO] mvn clean install -f org.nightlabs.eclipse/org.nightlabs.eclipse.parent/pom-aggregator.xml -DskipTests=$SKIPTESTS"
echo "[INFO] ------------------------------------------------------------------------"
mvn $MVN_GOALS -f org.nightlabs.eclipse/org.nightlabs.eclipse.parent/pom-aggregator.xml -DskipTests=$SKIPTESTS
if [[ $? != 0 ]] ; then    
    exit $?
fi

echo "[INFO] ------------------------------------------------------------------------"
echo "[INFO] mvn clean install -f org.nightlabs.jfire.min/org.nightlabs.jfire.min.aggregator/pom-aggregator.xml -DskipTests=$SKIPTESTS"
echo "[INFO] ------------------------------------------------------------------------"
mvn $MVN_GOALS -f org.nightlabs.jfire.min/org.nightlabs.jfire.min.aggregator/pom-aggregator.xml -DskipTests=$SKIPTESTS
if [[ $? != 0 ]] ; then    
    exit $?
fi

echo "[INFO] ------------------------------------------------------------------------"
echo "[INFO] mvn clean install -f org.nightlabs.jfire.min.eclipse/org.nightlabs.jfire.eclipse.min.aggregator/pom-aggregator.xml -DskipTests=$SKIPTESTS"
echo "[INFO] ------------------------------------------------------------------------"
mvn $MVN_GOALS -f org.nightlabs.jfire.min.eclipse/org.nightlabs.jfire.eclipse.min.aggregator/pom-aggregator.xml -DskipTests=$SKIPTESTS
if [[ $? != 0 ]] ; then    
    exit $?
fi

echo "[INFO] ------------------------------------------------------------------------"
echo "[INFO] mvn clean install -f org.nightlabs.jfire.max/org.nightlabs.jfire.max.aggregator/pom-aggregator.xml -DskipTests=$SKIPTESTS"
echo "[INFO] ------------------------------------------------------------------------"
mvn $MVN_GOALS -f org.nightlabs.jfire.maxs/org.nightlabs.jfire.max.aggregator/pom-aggregator.xml -DskipTests=$SKIPTESTS
if [[ $? != 0 ]] ; then    
    exit $?
fi

echo "[INFO] ------------------------------------------------------------------------"
echo "[INFO] mvn clean install -f org.nightlabs.jfire.max.eclipse/org.nightlabs.jfire.eclipse.max.aggregator/pom-aggregator.xml -DskipTests=$SKIPTESTS"
echo "[INFO] ------------------------------------------------------------------------"
mvn $MVN_GOALS -f org.nightlabs.jfire.max.eclipse/org.nightlabs.jfire.eclipse.max.aggregator/pom-aggregator.xml -DskipTests=$SKIPTESTS
if [[ $? != 0 ]] ; then    
    exit $?
fi
