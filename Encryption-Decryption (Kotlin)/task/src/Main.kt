package encryptdecrypt

import java.io.File

const val ALPHABET = "abcdefghijklmnopqrstuvwxyz"

class Cipher(private val args: Array<String>) {
    private var action = "enc"
    private var stringToEncode = ""
    private var key = 0
    private var out = ""
    private var algo = "shift"

    /**
     * Starts the encryption or decryption process based on command-line arguments.
     * Expects pairs of command and value arguments, where each pair represents a configuration parameter.
     * Supported parameters: "-mode", "-alg", "-key", "-data", "-in", "-out".
     * Prints an error message if the number of arguments is not even.
     * Processes the provided arguments and performs the specified encryption or decryption using the "shift" or "unicode" algorithm.
     */
    fun start() {
        if (args.size % 2 != 0) {
            println("Error with the arguments")
        } else {
            for (i in args.indices step 2) {
                when (args[i]) {
                    "-mode" -> action = args[i + 1]
                    "-alg" -> algo = args[i + 1]
                    "-key" -> key = args[i + 1].toInt()
                    "-data" -> stringToEncode = args[i + 1]
                    "-in" -> stringToEncode =  if(checkFile(args[i + 1])) File(args[i + 1]).readText() else ""
                    "-out" -> out = args[i + 1]
                }
            }

            when (algo) {
                "shift" -> shift(stringToEncode, key, if (action == "dec") ::sub else ::add)
                "unicode" -> unicode(stringToEncode, key, if (action == "dec") ::sub else ::add)
            }
        }

    }

    /**
     * Check if the file given exists
     * @param fileName The name of the file
     *
     * @return True if it exists, false if it doesn't
     */
    private fun checkFile(fileName: String): Boolean {
        if (File(fileName).exists()) {
            return true
        } else {
            println("Error with the file")
            return false
        }
    }

    /**
     * Encrypts or decrypts a message using a Unicode-based shifting method.
     *
     * @param message The message to encrypt or decrypt.
     * @param key A number used to determine the shift amount.
     * @param operation The operation to be applied during the Unicode-based shift.
     *                  For encryption, it is addition.
     *                  For decryption, it is subtraction.
     */
    private fun unicode(message: String, key: Int, operation: (Int, Int) -> Int) {
        var result = ""
        message.forEach { result += operation(it.code, key).toChar() }
        if (out == "") {
            println(result)
        } else {
            val myFile = File(out)
            myFile.writeText(result)
        }
    }

    /**
     * Encrypts or decrypts a message using a shifting method.
     *
     * @param message The message to encrypt or decrypt.
     * @param key A number used to determine the shift amount.
     * @param operation The operation to be applied during the shift.
     *                  For encryption, it is addition.
     *                  For decryption, it is subtraction.
     */
    private fun shift(message: String, key: Int, operation: (Int, Int) -> Int) {
        var result = ""

        message.forEach {ch ->
            var letterIndex = operation(ALPHABET.indexOf(ch.lowercaseChar()), key)
            if (letterIndex < 0) letterIndex += ALPHABET.length
            val encodedLetter = if (ch.isUpperCase()) ALPHABET[letterIndex % ALPHABET.length].uppercase() else ALPHABET[letterIndex % ALPHABET.length]
            result += if (ch.isLetter()) encodedLetter else ch
        }

        if (out == "") {
            println(result)
        } else {
            val myFile = File(out)
            myFile.writeText(result)
        }
    }

    /**
     * Adds two numbers
     * @param n1 The first number
     * @param n2 The second number
     *
     * @return The sum of n1 and n2
     */
    private fun add(n1: Int, n2: Int): Int = n1 + n2

    /**
     * Subtracts two numbers
     * @param n1: The first number
     * @param n2: The second number
     *
     * @return The result of subtracting n2 from n1.
     */
    private fun sub(n1: Int, n2: Int): Int = n1 - n2
}

fun main(args: Array<String>) {
    val cipher = Cipher(args)
    cipher.start()
}