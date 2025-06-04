"use client"

import type React from "react"

import { useState } from "react"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Loader2, CheckCircle, Sparkles, Info, Zap, GitBranch, ArrowRight } from "lucide-react"
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip"

interface GeneratedDiagram {
  title: string
  classNames: string[]
  timestamp: Date
}

export default function AIUMLGenerator() {
  const [prompt, setPrompt] = useState("")
  const [title, setTitle] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const [generatedDiagram, setGeneratedDiagram] = useState<GeneratedDiagram | null>(null)
  const [showSuccess, setShowSuccess] = useState(false)

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!prompt.trim() || !title.trim()) {
      return
    }

    setIsLoading(true)
    setGeneratedDiagram(null)
    setShowSuccess(false)

    // Simulate AI processing
    try {
      await new Promise((resolve) => setTimeout(resolve, 3000))

      const mockClassNames = extractClassNames(prompt)

      setGeneratedDiagram({
        title: title.trim(),
        classNames: mockClassNames,
        timestamp: new Date(),
      })

      setShowSuccess(true)
    } catch (error) {
      console.error("Error generating diagram:", error)
    } finally {
      setIsLoading(false)
    }
  }

  const extractClassNames = (text: string): string[] => {
    const commonClasses = [
      "User",
      "Account",
      "Product",
      "Order",
      "Customer",
      "Payment",
      "Database",
      "Service",
      "Controller",
      "Model",
      "Repository",
      "Manager",
      "Handler",
      "Processor",
      "Validator",
      "Entity",
      "Authentication",
      "Session",
      "Profile",
      "Transaction",
    ]

    const words = text.toLowerCase().split(/\s+/)
    const foundClasses = commonClasses.filter((className) =>
      words.some((word) => word.includes(className.toLowerCase())),
    )

    return foundClasses.length > 0 ? foundClasses.slice(0, 6) : ["User", "Service", "Repository"]
  }

  const resetForm = () => {
    setPrompt("")
    setTitle("")
    setGeneratedDiagram(null)
    setShowSuccess(false)
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-purple-900 to-slate-900">
      {/* Animated Background */}
      <div className="absolute inset-0 bg-gradient-to-br from-slate-900/50 via-purple-900/30 to-slate-900/50 opacity-40"></div>

      {/* Navigation Bar */}
      <nav className="relative z-10 bg-black/20 backdrop-blur-xl border-b border-white/10 shadow-2xl">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between h-16">
            <div className="flex items-center space-x-3">
              <div className="p-2 bg-gradient-to-r from-indigo-500 to-purple-600 rounded-lg">
                <GitBranch className="h-6 w-6 text-white" />
              </div>
              <h1 className="text-xl font-bold bg-gradient-to-r from-white to-gray-300 bg-clip-text text-transparent">
                AI UML Generator
              </h1>
            </div>
            <div className="flex items-center space-x-2">
              <div className="px-3 py-1 bg-gradient-to-r from-indigo-500/20 to-purple-600/20 rounded-full border border-indigo-400/30">
                <span className="text-xs font-medium text-indigo-300">Beta</span>
              </div>
            </div>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <main className="relative z-10 max-w-4xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
        <div className="space-y-8">
          {/* Hero Section */}
          <div className="text-center space-y-4">
            <div className="inline-flex items-center space-x-2 px-4 py-2 bg-gradient-to-r from-indigo-500/10 to-purple-600/10 rounded-full border border-indigo-400/20">
              <Sparkles className="h-4 w-4 text-indigo-400" />
              <span className="text-sm font-medium text-indigo-300">Powered by AI</span>
            </div>
            <h2 className="text-4xl sm:text-5xl font-bold bg-gradient-to-r from-white via-gray-100 to-gray-300 bg-clip-text text-transparent">
              Generate UML Diagrams
            </h2>
            <p className="text-lg text-gray-400 max-w-2xl mx-auto">
              Transform your system descriptions into professional UML class diagrams using advanced AI
            </p>
          </div>

          {/* Generator Card */}
          <Card className="bg-white/5 backdrop-blur-xl border border-white/10 shadow-2xl">
            <CardHeader className="text-center pb-8">
              <h3 className="text-2xl font-semibold text-white mb-2">Describe your system</h3>
              <p className="text-gray-400">Explain your software architecture in natural language</p>
            </CardHeader>
            <CardContent className="space-y-8">
              <form onSubmit={handleSubmit} className="space-y-6">
                {/* System Description */}
                <div className="space-y-2">
                  <div className="flex items-center space-x-2">
                    <Label htmlFor="prompt" className="text-sm font-medium text-gray-300">
                      System Description
                    </Label>
                    <TooltipProvider>
                      <Tooltip>
                        <TooltipTrigger asChild>
                          <Info className="h-4 w-4 text-gray-500 hover:text-gray-400 cursor-help" />
                        </TooltipTrigger>
                        <TooltipContent className="max-w-xs bg-gray-900 border-gray-700">
                          <p className="text-sm">
                            Example: "An e-commerce platform with users, products, shopping carts, orders, and payment
                            processing"
                          </p>
                        </TooltipContent>
                      </Tooltip>
                    </TooltipProvider>
                  </div>
                  <div className="relative">
                    <Textarea
                      id="prompt"
                      placeholder="Describe your system architecture, components, and relationships..."
                      value={prompt}
                      onChange={(e) => setPrompt(e.target.value)}
                      className="min-h-[140px] bg-white/5 border-white/20 text-white placeholder:text-gray-500 focus:border-indigo-400/50 focus:ring-indigo-400/20 resize-none backdrop-blur-sm"
                      disabled={isLoading}
                    />
                    <div className="absolute bottom-3 right-3">
                      <span className="text-xs text-gray-500">{prompt.length}/500</span>
                    </div>
                  </div>
                </div>

                {/* Diagram Title */}
                <div className="space-y-2">
                  <Label htmlFor="title" className="text-sm font-medium text-gray-300">
                    Diagram Title
                  </Label>
                  <Input
                    id="title"
                    type="text"
                    placeholder="Enter a descriptive title for your diagram"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    className="bg-white/5 border-white/20 text-white placeholder:text-gray-500 focus:border-indigo-400/50 focus:ring-indigo-400/20 backdrop-blur-sm"
                    disabled={isLoading}
                  />
                </div>

                {/* Submit Button */}
                <Button
                  type="submit"
                  disabled={isLoading || !prompt.trim() || !title.trim()}
                  className="w-full bg-gradient-to-r from-indigo-600 to-purple-600 hover:from-indigo-700 hover:to-purple-700 text-white font-semibold py-3 px-6 rounded-lg shadow-lg hover:shadow-xl transition-all duration-200 disabled:opacity-50 disabled:cursor-not-allowed group"
                >
                  {isLoading ? (
                    <>
                      <Loader2 className="mr-2 h-5 w-5 animate-spin" />
                      <span>Generating Diagram...</span>
                    </>
                  ) : (
                    <>
                      <Zap className="mr-2 h-5 w-5 group-hover:scale-110 transition-transform" />
                      <span>Generate UML Diagram</span>
                      <ArrowRight className="ml-2 h-4 w-4 group-hover:translate-x-1 transition-transform" />
                    </>
                  )}
                </Button>
              </form>
            </CardContent>
          </Card>

          {/* Success Message */}
          {showSuccess && generatedDiagram && (
            <Card className="bg-gradient-to-r from-emerald-500/10 to-teal-500/10 backdrop-blur-xl border border-emerald-400/20 shadow-2xl animate-in slide-in-from-bottom-4 duration-500">
              <CardContent className="pt-6">
                <div className="flex items-start space-x-4">
                  <div className="p-2 bg-emerald-500/20 rounded-full">
                    <CheckCircle className="h-6 w-6 text-emerald-400" />
                  </div>
                  <div className="flex-1 space-y-4">
                    <div>
                      <h3 className="text-xl font-semibold text-white mb-2">Diagram generated and saved!</h3>
                      <p className="text-emerald-200">
                        Your UML class diagram "{generatedDiagram.title}" has been successfully created.
                      </p>
                    </div>

                    {/* Generated Classes */}
                    {generatedDiagram.classNames.length > 0 && (
                      <div className="space-y-3">
                        <h4 className="font-medium text-emerald-100">Detected Classes:</h4>
                        <div className="grid grid-cols-2 sm:grid-cols-3 gap-2">
                          {generatedDiagram.classNames.map((className, index) => (
                            <div
                              key={index}
                              className="px-3 py-2 bg-white/10 backdrop-blur-sm rounded-lg border border-white/20 text-center animate-in fade-in-50 duration-300"
                              style={{ animationDelay: `${index * 100}ms` }}
                            >
                              <span className="text-sm font-medium text-white">{className}</span>
                            </div>
                          ))}
                        </div>
                      </div>
                    )}

                    {/* Action Buttons */}
                    <div className="flex flex-col sm:flex-row gap-3 pt-2">
                      <Button
                        onClick={resetForm}
                        variant="outline"
                        className="border-emerald-400/30 text-emerald-300 hover:bg-emerald-500/10 backdrop-blur-sm"
                      >
                        Generate Another
                      </Button>
                      <Button
                        variant="outline"
                        className="border-emerald-400/30 text-emerald-300 hover:bg-emerald-500/10 backdrop-blur-sm"
                      >
                        View Diagram
                      </Button>
                      <Button
                        variant="outline"
                        className="border-emerald-400/30 text-emerald-300 hover:bg-emerald-500/10 backdrop-blur-sm"
                      >
                        Export PNG
                      </Button>
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          )}

          {/* Features Grid */}
          <div className="grid md:grid-cols-3 gap-6 mt-16">
            {[
              {
                icon: Sparkles,
                title: "AI-Powered",
                description: "Advanced natural language processing to understand your system descriptions",
              },
              {
                icon: Zap,
                title: "Instant Generation",
                description: "Generate professional UML diagrams in seconds, not hours",
              },
              {
                icon: GitBranch,
                title: "Export Ready",
                description: "Export your diagrams in multiple formats for documentation and presentations",
              },
            ].map((feature, index) => (
              <Card
                key={index}
                className="bg-white/5 backdrop-blur-xl border border-white/10 hover:bg-white/10 transition-all duration-300"
              >
                <CardContent className="pt-6 text-center">
                  <div className="p-3 bg-gradient-to-r from-indigo-500/20 to-purple-600/20 rounded-full w-fit mx-auto mb-4">
                    <feature.icon className="h-6 w-6 text-indigo-400" />
                  </div>
                  <h3 className="font-semibold text-white mb-2">{feature.title}</h3>
                  <p className="text-sm text-gray-400">{feature.description}</p>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </main>
    </div>
  )
}
