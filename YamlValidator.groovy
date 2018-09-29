
@Grapes([
    @Grab(group = 'org.yaml', module = 'snakeyaml', version = '1.20')
])
import groovy.transform.Field
import java.util.logging.Logger
import java.util.logging.Level
import org.yaml.snakeyaml.Yaml
import java.util.function.Supplier

System.setProperty('java.util.logging.SimpleFormatter.format', '%5$s%n')
@Field static final Logger LOGGER = Logger.getLogger('YamlValidator.log')
@Field boolean debug

def cli = new CliBuilder(usage: 'groovy YamlValidator.groovy [options]')
cli._(longOpt: 'debug', args: 1, argName: 'debug', 'Flag to turn on debug logging')

def options = cli.parse(args)
debug = Boolean.parseBoolean(options.debug)

@Field Yaml yaml = new Yaml()
validateYamlFiles(new File(System.properties['user.dir']))

/**
 * Validates all yaml files in the provided directory recursively
 *
 * @param directory
 * @return
 */
def validateYamlFiles(File directory) {
    debugLog({ "Validating files in '${directory}'".toString() })

    String fileName
    directory.eachFile { file ->
        if (file.isDirectory()) {
            // Recursively evaluate yaml files in each folder
            validateYamlFiles(file)
        } else if (file.isFile()) {
            fileName = file.absolutePath
            if (fileName.endsWith('.yaml') || fileName.endsWith('.yml')) {
                debugLog({"Validating '$fileName'.".toString()})
                int index = 1

                file.withInputStream { yamlFileInputStream ->
                    index = 1

                    try {
                        yaml.loadAll(yamlFileInputStream).each { document ->
                            debugLog({ "Document $index of '$fileName' is valid".toString() })
                            index++
                        }
                    }
                    catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "'${fileName}' is invalid", e)
                        System.exit(1)
                    }
                }
                LOGGER.info("'$fileName' is valid")
            }
        }
    }
}

/**
 * Log the provided message if debug is enabled
 *
 * @param message
 */
void debugLog(Supplier message) {
    if (debug) {
        LOGGER.info(message)
    }
}