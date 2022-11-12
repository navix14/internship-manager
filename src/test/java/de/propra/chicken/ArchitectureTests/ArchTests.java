package de.propra.chicken.ArchitectureTests;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import de.propra.chicken.ChickenApplication;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchIgnore;
import com.tngtech.archunit.junit.ArchTest;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;

@AnalyzeClasses(packages = "de.propra.chicken", importOptions = { ImportOption.DoNotIncludeTests.class })
public class ArchTests {
        @ArchTest
        ArchRule rule_1 = onionArchitecture()
                .domainModels("..domain.model..", "..domain.utils..")
                .domainServices("..domain.repositories..")
                .applicationServices("..services..")
                .adapter("infrastructure", "..infrastructure..");
}
