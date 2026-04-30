# ---- Stage 1: Build ----
# We compile the code inside Docker so anyone can build this project
# without having Java or Maven installed on their machine.
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy the Maven wrapper script and its config.
# mvnw is not Maven itself — it is a script that downloads the exact Maven version
# pinned in .mvn/wrapper/maven-wrapper.properties, ensuring consistent builds.
# eclipse-temurin only includes Java, so this is how we get Maven.
COPY .mvn .mvn
COPY mvnw mvnw.cmd pom.xml ./

# Download all dependencies before copying source code.
# Docker caches each line as a layer — dependencies change rarely but source code
# changes constantly, so splitting them means Docker only re-downloads dependencies
# when pom.xml changes, not on every code change.
RUN ./mvnw dependency:go-offline -q

# Now copy source code and compile into a .jar
COPY src ./src
RUN ./mvnw package -DskipTests -q

# ---- Stage 2: Run ----
# Start from a clean image and copy only the compiled .jar from Stage 1.
# This throws away Maven, the source code, and all downloaded dependencies —
# the final image is small and does not expose your source code.
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
