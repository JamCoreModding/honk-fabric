import {
  Input,
  prompt,
} from "https://deno.land/x/cliffy@v0.25.2/prompt/mod.ts";
import { join } from "https://deno.land/std@v0.152.0/path/mod.ts";

const options = await prompt([
  {
    name: "mod_name",
    message: "Enter mod name:",
    type: Input,
  },
  {
    name: "mod_id",
    message: "Enter mod ID:",
    type: Input,
  },
  {
    name: "main_class_name",
    message: "Enter main class name:",
    type: Input,
  },
  {
    name: "maven_group",
    message: "Enter maven group:",
    type: Input,
    default: "io.github.jamalam360",
  },
  {
    name: "description",
    message: "Enter description:",
    type: Input,
  },
  {
    name: "author",
    message: "Enter author:",
    type: Input,
    default: "Jamalam",
  },
  {
    name: "github_user",
    message: "Enter github user/organisation:",
    type: Input,
    default: "JamCoreModding",
  },
  {
    name: "github_repo",
    message: "Enter github repo:",
    type: Input,
  },
]);

const mainPackage = `${options.maven_group}/${
  options.mod_id!.split("_").join("/").split("-").join("/")
}`.replaceAll(".", "/");

const mainClass = join(
  Deno.cwd(),
  "src/main/java",
  mainPackage.replaceAll(".", "/"),
  `${options.main_class_name}.java`,
);

await transformMainPackage();
await transformMainClass();
await transformAssetsDirectory();
await transformQuiltModJson();
await transformMixinsJson();
await transformGradleProperties();
await transformReadme();
await transformLicense();
await transformChangelogs();

async function transformMainPackage() {
  await Deno.mkdir(
    join(Deno.cwd(), "src/main/java", mainPackage.replaceAll(".", "/")),
    { recursive: true },
  );

  await Deno.rename(
    join(Deno.cwd(), "src/main/java/io/github/jamalam360/templatemod"),
    join(Deno.cwd(), "src/main/java", mainPackage.replaceAll(".", "/")),
  );
}

async function transformMainClass() {
  await Deno.rename(
    join(Deno.cwd(), "src/main/java", mainPackage, `TemplateModInit.java`),
    mainClass,
  );

  const content = await Deno.readTextFile(mainClass);

  await Deno.writeTextFile(
    mainClass,
    content.replaceAll("TemplateModInit", options.main_class_name!)
      .replaceAll("templatemod", options.mod_id!)
      .replaceAll("Template Mod", options.mod_name!)
      .replaceAll(
        "io.github.jamalam360.templatemod",
        mainPackage.replaceAll("/", "."),
      ),
  );
}

async function transformAssetsDirectory() {
  await Deno.rename(
    join(Deno.cwd(), "src/main/resources/assets/templatemod"),
    join(Deno.cwd(), "src/main/resources/assets", options.mod_id!),
  );
}

async function transformQuiltModJson() {
  const qmj = `./src/main/resources/quilt.mod.json`;
  const qmjContent = await Deno.readTextFile(qmj);
  await Deno.writeTextFile(
    qmj,
    qmjContent
      .replaceAll(
        "io.github.jamalam360.templatemod.TemplateModInit",
        `${mainPackage}.${options.main_class_name}`,
      )
      .replaceAll("io.github.jamalam360", options.maven_group!)
      .replaceAll("templatemod", options.mod_id!)
      .replaceAll("Template Mod", options.mod_name!)
      .replaceAll("A Quilt mod template", options.description!)
      .replaceAll("Jamalam", options.author!)
      .replaceAll("JamCoreModding", options.github_user!)
      .replaceAll("TemplateMod", options.github_repo!),
  );
}

async function transformMixinsJson() {
  const mixins = join(
    Deno.cwd(),
    "src/main/resources",
    `${options.mod_id}.mixins.json`,
  );
  await Deno.rename(
    join(Deno.cwd(), "src/main/resources/templatemod.mixins.json"),
    mixins,
  );
  const mixinsContent = await Deno.readTextFile(mixins);
  await Deno.writeTextFile(
    mixins,
    mixinsContent.replaceAll(
      "io.github.jamalam360.templatemod",
      mainPackage.replaceAll("/", "."),
    ),
  );
}

async function transformGradleProperties() {
  const gradleProperties = join(Deno.cwd(), "gradle.properties");
  const gradlePropertiesContent = await Deno.readTextFile(gradleProperties);
  await Deno.writeTextFile(
    gradleProperties,
    gradlePropertiesContent
      .replaceAll("FabricTemplateMod", options.github_repo!)
      .replaceAll("TemplateMod", options.github_repo!)
      .replaceAll("JamCoreModding", options.github_user!)
      .replaceAll("template-mod", options.mod_id!.replaceAll("_", "-")),
  );
}

async function transformReadme() {
  await Deno.writeTextFile(
    "./README.md",
    `
# ${options.mod_name}

${options.description}
        `.trim(),
  );
}

async function transformLicense() {
  await Deno.writeTextFile(
    "./LICENSE",
    await (await Deno.readTextFile("./LICENSE")).replaceAll(
      "Jamalam",
      options.author!,
    ).replaceAll("[YEAR]", new Date().getFullYear().toString()),
  );
}

async function transformChangelogs() {
  await Deno.writeTextFile(
    "./CHANGELOG_TEMPLATE.md",
    (await Deno.readTextFile("./CHANGELOG_TEMPLATE.md"))
      .replaceAll("FabricTemplateMod", options.github_repo!)
      .replaceAll("TemplateMod", options.github_repo!)
      .replaceAll("JamCoreModding", options.github_user!),
  );

  await Deno.writeTextFile(
    "./CHANGELOG.md",
    await Deno.readTextFile("./CHANGELOG_TEMPLATE.md"),
  );
}
