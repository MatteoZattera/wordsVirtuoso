fun main(vararg filenames: String) = when {
    filenames.size != 2 -> "Error: Wrong number of arguments."
    !File(filenames[0]).exists() -> "Error: The words file ${filenames[0]} doesn't exist."
    !File(filenames[1]).exists() -> "Error: The candidate words file ${filenames[1]} doesn't exist."
    else -> {
        when {
            File(filenames[0]).amountOfInvalidWords > 0 -> "Error: ${File(filenames[0]).amountOfInvalidWords} invalid words were found in the ${filenames[0]} file."
            File(filenames[1]).amountOfInvalidWords > 0 -> "Error: ${File(filenames[1]).amountOfInvalidWords} invalid words were found in the ${filenames[1]} file."
            else -> {
                val lowercaseWords = File(filenames[0]).readLines().map { it.lowercase() }
                File(filenames[1]).readLines().map { it.lowercase() }.count { !lowercaseWords.contains(it) }.let {
                    if (it > 0) "Error: $it candidate words are not included in the ${filenames[0]} file." else { runGame(*filenames); null }
                }
            }
        }
    }
}.printlnIt()

private fun runGame(vararg filenames: String) {
    val words = File(filenames[0]).readLines().map { it.uppercase() }
    val candidateWords = File(filenames[1]).readLines().map { it.uppercase() }
    "Words Virtuoso".printlnIt()

    val secretWord = candidateWords.random().also { println(it) }
    val clueStrings = mutableListOf<String>()
    val wrongCharacters = mutableSetOf<Char>()
    var attempts = 0
    val startTime = System.currentTimeMillis()
    while (true) {
        "Input a 5-letter word:".printlnIt()
        val inputWord = readln().uppercase()
        attempts++
        when {
            inputWord == "EXIT" -> {
                "The game is over.".printlnIt()
                break
            }
            inputWord == secretWord -> {
                if (attempts == 1) {
                    secretWord.map { "\u001B[48:5:10m${it}\u001B[0m" }.joinToString("").printlnIt()
                    "Correct!\nAmazing luck! The solution was found at once.".printlnIt()
                } else {
                    clueStrings.joinToString("\n").printlnIt()
                    secretWord.map { "\u001B[48:5:10m${it}\u001B[0m" }.joinToString("").printlnIt()
                    "Correct!\nThe solution was found after $attempts tries in ${(System.currentTimeMillis() - startTime) / 1000} seconds.".printlnIt()
                }
                break
            }
            inputWord.hasNotFiveCharacters() -> "The input isn't a 5-letter word.".printlnIt()
            inputWord.hasInvalidCharacters() -> "One or more letters of the input aren't valid.".printlnIt()
            inputWord.hasDuplicateCharacters() -> "The input has duplicate letters.".printlnIt()
            inputWord !in words -> "The input word isn't included in my words list.".printlnIt()
            else -> {
                val builder = StringBuilder()
                for (i in inputWord.indices) {
                    builder.append(when {
                        secretWord[i] == inputWord[i] -> "\u001B[48:5:10m${inputWord[i]}\u001B[0m"
                        secretWord.contains(inputWord[i]) -> "\u001B[48:5:11m${inputWord[i]}\u001B[0m"
                        else -> "\u001B[48:5:7m${inputWord[i]}\u001B[0m".also { wrongCharacters.add(inputWord[i]) }
                    })
                }
                clueStrings.add(builder.toString())
                clueStrings.joinToString("\n").printlnIt()
                "\n\u001B[48:5:14m${wrongCharacters.sorted().joinToString("")}\u001B[0m".printlnIt()
            }
        }
    }
}

private val File.amountOfInvalidWords get() = this.readLines().count { it.hasNotFiveCharacters() || it.hasInvalidCharacters() || it.hasDuplicateCharacters() }
private fun String.hasNotFiveCharacters() = this.length != 5
private fun String.hasInvalidCharacters() = this.contains("[^a-zA-Z]".toRegex())
private fun String.hasDuplicateCharacters() = this.length != this.toCharArray().distinct().size
private fun String?.printlnIt() = if (this != null) println(this) else Unit
