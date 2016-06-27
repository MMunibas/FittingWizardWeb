package ch.unibas.fitting.web.ljfit.step4;

import ch.unibas.fitting.shared.charmm.web.CharmmResult;
import ch.unibas.fitting.shared.charmm.web.CharmmResultParser;
import ch.unibas.fitting.shared.charmm.web.CharmmResultParserOutput;
import ch.unibas.fitting.shared.workflows.charmm.CharmmInputContainer;
import ch.unibas.fitting.web.ljfit.CharmmRepository;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Created by tobias on 27.06.16.
 */
public class ParseResultsCommand {
    @Inject
    private CharmmRepository charmmRepository;

    public CharmmResultParserOutput run(String username) {
        Optional<CharmmResult> charmmResult = charmmRepository.getResultFor(username);
        Optional<CharmmInputContainer> container = charmmRepository.getContainerFor(username);

        CharmmResultParserOutput output = new CharmmResultParserOutput();
        if (charmmResult.isPresent() && container.isPresent()) {
            output = CharmmResultParser.parseOutput(
                    charmmResult.get(),
                    container.get().getGasVdw(),
                    container.get().getGasMtp(),
                    container.get().getSolvVdw(),
                    container.get().getSolvMtp());
        }
        return output;
    }
}
